package com.casdonline.graduation;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

public class SetSpawn implements Listener, CommandExecutor {

	private Main instance;
	
	public SetSpawn(Main instance) {
		this.instance = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("setspawn")) {
			if(args.length < 2 || args[0].equalsIgnoreCase("help")) { sender.sendMessage(getHelp("default")); return true; }
			if(args[0].equalsIgnoreCase("help")) { sender.sendMessage(getHelp(args[1])); return true; }
			if(args[0].equalsIgnoreCase("set")) {
				Team team = TeamManager.teamMap.get(args[1]);
				if(team == null) { sender.sendMessage(TextFormat.RED + "There is not a team with that name!"); return true; }
				if(args.length < 5 && !(sender instanceof Player)) { sender.sendMessage(TextFormat.RED + "Only players can use this command without exact coords!"); return true; }
				if(args.length == 2) { team.editSpawn(((Player) sender).getPosition()); }
				if(args.length == 3) { sender.sendMessage(TextFormat.RED + "You must have at least the X and Z coords!"); return true; }
				if(args.length == 4) { team.editSpawn(new Position(Double.parseDouble(args[2]), ((Player) sender).getPosition().getY(), Double.parseDouble(args[3]))); }
				if(args.length >= 5) { team.editSpawn(new Position(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]))); }
				sender.sendMessage(TextFormat.GREEN + "Successfully set the spawn for " + team.getName() +
						" to " + team.getSpawnX() + ", " + team.getSpawnY() + ", " + team.getSpawnZ()); return true;
			}
			if(args[0].equalsIgnoreCase("reset")) {
				Team team = TeamManager.teamMap.get(args[1]);
				if(team == null) { sender.sendMessage(TextFormat.RED + "There is not a team with that name!"); return true; }
				team.editSpawn(instance.getServer().getDefaultLevel().getSpawnLocation());
				sender.sendMessage(TextFormat.GREEN + "Successfully reset the spawn for " + team.getName() +
						" to " + team.getSpawnX() + ", " + team.getSpawnY() + ", " + team.getSpawnZ()); return true;
			}
			if(args[0].equalsIgnoreCase("check")) {
				Team team = TeamManager.teamMap.get(args[1]);
				if(team == null) { sender.sendMessage(TextFormat.RED + "There is not a team with that name!"); return true; }
				sender.sendMessage(TextFormat.AQUA + "The spawn for the team " + team.getName() + " is at " + team.getSpawnX() + ", " + team.getSpawnY() + ", " + team.getSpawnZ());
				return true;
			}
			sender.sendMessage(TextFormat.RED + "Unknown subcommand! Please use \"/setspawn help\" for help!"); return true;
		}
		return false;
	}
	
	private String getHelp(String str) {
		switch(str) {
		case "default":
			return TextFormat.GREEN + "" + TextFormat.BOLD + "SetSpawn subcommands: \n"
				+ TextFormat.RESET + TextFormat.GOLD + "help" + TextFormat.RESET + ": Shows information on command and subcommands\n"
				+ TextFormat.RESET + TextFormat.GOLD + "set"  + TextFormat.RESET + ": Sets a team's spawn location\n"
				+ TextFormat.RESET + TextFormat.GOLD + "reset" + TextFormat.RESET + ": Resets an existing team's spawn location to the default spawn location\n"
				+ TextFormat.RESET + TextFormat.GOLD + "check" + TextFormat.RESET + ": Checks an existing team's spawn location";
		case "help":
			return TextFormat.GREEN + "Help Command\n"
					+ TextFormat.RESET + "Shows information on subcommands (Things in parenthesis are optional)\n"
					+ TextFormat.RED + "Usage: /setspawn help <subcommand>";
		case "set":
			return TextFormat.GREEN + "Set Command\n"
					+ TextFormat.RESET + "Sets a team's spawn location\n"
					+ getUsage("set");
		case "reset":
			return TextFormat.GREEN + "Reset Command\n"
			+ TextFormat.RESET + "Resets an existing team's spawn location to the default spawn location\n"
			+ getUsage("reset");
		case "check":
			return TextFormat.GREEN + "Check Command\n"
			+ TextFormat.RESET + "Checks an existing team's spawn location\n"
			+ getUsage("check");
		default:
			return TextFormat.RED + "Unknown subcommand! Please make sure you spelled it correct!\n"
					+ TextFormat.RED + "If you need the list of subcommands, please use \"/setspawn help\"";
		}
	}
	private static String getUsage(String str) {
		switch(str) {
			case "default":
				return TextFormat.RED + "Usage: /setspawn <subcommand> <arguments if needed>";
			case "set":
				return TextFormat.RED + "Usage: /setspawn set <Team Name> (X, Y, Z)";
			case "reset":
				return TextFormat.RED + "Usage: /setspawn reset <Team Name>";
			case "check":
				return TextFormat.RED + "Usage: /setspawn check <Team Name>";
			default:
				return TextFormat.RED + "Usage: /setspawn <subcommand>";
		}
	}
}
