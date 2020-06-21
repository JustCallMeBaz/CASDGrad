package com.casdonline.graduation;

import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.block.BlockStairs;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartEmpty;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityVehicleExitEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.TextFormat;

public class ChairEvent implements Listener {
		
	//public static BiMap<BlockStairs, EntityMinecartEmpty> map = HashBiMap.create();
	public static Map<BlockStairs, EntityMinecartEmpty> map = new HashMap<>();
	public static Map<EntityMinecartEmpty, BlockStairs> reverseMap = new HashMap<>();
	
	@EventHandler
	public void onStairRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(player.isSneaking() || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(player.getInventory().getItemInHand().getId() != Item.AIR) return;
		if(!(event.getBlock() instanceof BlockStairs)) return;
		BlockStairs block = (BlockStairs) event.getBlock();
		if(map.get(block) != null && !map.get(block).isPassenger(player)) { event.getPlayer().sendMessage(TextFormat.RED + "This seat is occupied!"); return; }
		if(map.get(block) != null && map.get(block).isPassenger(player)) return;
		for(EntityMinecartEmpty ent : reverseMap.keySet()) {
			if(ent.isPassenger(player)) { removeFromMap(ent); ent.close(); }
		}
		double x = block.getLocation().x, y = block.getLocation().y, z = block.getLocation().z;
		EntityMinecartEmpty ent = (EntityMinecartEmpty) Entity.createEntity(84, new Position(toMid(x), y, toMid(z), block.getLevel()));
		ent.addMotion(10, 0, 0);
		ent.addMovement(1000, 0, 0, 0, 0, 0);
		
		BlockFace face = block.getBlockFace();
		int rotation = 90;
		if(face == BlockFace.NORTH || face == BlockFace.SOUTH)
			rotation = 180;
		ent.setRotation(rotation, 0);
		ent.entityCollisionReduction = 0.89;
		ent.scheduleUpdate();
		map.put(block, ent);
		reverseMap.put(ent, block);
		ent.spawnToAll();
		ent.mountEntity(player);
	}
	
	@EventHandler
	public void onDismount(EntityVehicleExitEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!map.containsValue(event.getVehicle())) return;
		EntityMinecartEmpty ent = (EntityMinecartEmpty) event.getVehicle();
		removeFromMap(ent);
		ent.close();
	}
	
	@SuppressWarnings("unused")
	private void removeFromMap(BlockStairs block) { reverseMap.remove(map.get(block)); map.remove(block); }
	
	private void removeFromMap(EntityMinecartEmpty ent) { map.remove(reverseMap.get(ent)); reverseMap.remove(ent); }
	
	//To cut off the decimals, then put 0.5 on it instead
	private double toMid(double num) { return (((double)((int) num)) + 0.5); }
	
}
