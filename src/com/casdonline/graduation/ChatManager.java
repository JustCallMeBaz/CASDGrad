package com.casdonline.graduation;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.utils.TextFormat;

public class ChatManager implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@EventHandler
	public void onChatEvent(PlayerChatEvent event) {
		if(UserManager.userMap.get(event.getPlayer().getDisplayName()) == null || 
			!UserManager.userMap.get(event.getPlayer().getDisplayName()).getTeam().getName().equals("Operators")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(TextFormat.DARK_RED + "You do not have permission to chat!");
		}
	}

}
