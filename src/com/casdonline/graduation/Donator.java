package com.casdonline.graduation;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemChestplateLeather;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.item.ItemFirework.FireworkExplosion;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;

public class Donator implements CommandExecutor {

	private static Item[] donatorItems = createDonatorItems();
	
	private static Main instance;
	public Donator(Main instance) {
		Donator.instance = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { 
		if(label.equalsIgnoreCase("donator")) {
			
			if(args[0].equalsIgnoreCase("give")) {
				if(!Donator.givePlayerItems(args[1])) {
					sender.sendMessage(TextFormat.RED + "This player does not exist!");
					return true;
				}
				
				
			}
		}
		return false; 
	}
	
	public static void givePlayerItems(Player player) {
		for(Item item : donatorItems) player.getInventory().addItem(item);
	}
	
	public static boolean givePlayerItems(String playerName) {
		Player player = instance.getServer().getPlayer(playerName);
		if(player == null || !player.isOnline()) return false;
		
		Donator.givePlayerItems(player);
		
		return true;
	}
	
	private static Item[] createDonatorItems() {
		List<Item> items = new ArrayList<>();
		ItemChestplateLeather chestplate = new ItemChestplateLeather();
		chestplate.setColor(0, 0, 128);
		chestplate.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY).setLevel(100));
		String lore = "\n";
		lore += TextFormat.DARK_PURPLE + "Right click this item to put it on to show your support for the graduates!";
		chestplate.setLore(lore);
		items.add(chestplate);
		ItemFirework firework = new ItemFirework();
		lore = "\n";
		lore += TextFormat.DARK_PURPLE + "Use these by right clicking the ground during the graduation!";
		firework.setCount(3);
		firework.addExplosion(new FireworkExplosion().type(FireworkExplosion.ExplosionType.LARGE_BALL)
				.addColor(DyeColor.BROWN).addColor(DyeColor.BLUE).addColor(DyeColor.WHITE).setTrail(true));
		items.add(firework);
		return items.toArray(new Item[items.size()]);
	}
	
}
