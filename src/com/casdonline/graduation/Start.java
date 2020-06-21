package com.casdonline.graduation;

import java.util.ArrayList;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class Start implements CommandExecutor {

	private Main instance;
	public static ArrayList<StartObject> objects = new ArrayList<>();
	
	public Start(Main instance) {
		this.instance = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//if(objects.size() < 1)
		if(label.equalsIgnoreCase("start")) {
			if(!StartThread.started)
				new StartThread(instance, objects, instance.getServer().getOnlinePlayers().values());
			else 
				sender.sendMessage(TextFormat.RED + "This command is already running!");
			return true;
		}
		return false;
	}
}
