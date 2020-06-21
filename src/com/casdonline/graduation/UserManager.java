package com.casdonline.graduation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class UserManager implements CommandExecutor {
	public static ArrayList<MinecraftUser> users = new ArrayList<>();
	public static LinkedHashMap<String, MinecraftUser> userMap = new LinkedHashMap<>();
	
	public static boolean playerExists(String playerName) {
		if(UserManager.userMap.get(playerName) == null)
			return false;
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("user")) {
			if(sender instanceof Player && UserManager.userMap.get(((Player) sender).getDisplayName()) != null 
					&& !UserManager.userMap.get(((Player) sender).getDisplayName()).getTeam().getName().equals("Operators")) { 
				sender.sendMessage(TextFormat.DARK_RED + "You do not have permission to use this command!"); return true; 
			}
			if(args.length < 1 || (args[0].equalsIgnoreCase("help") && args.length < 2)) {
				sender.sendMessage(getHelp("default"));
				return true;
			}
			if(args[0].equalsIgnoreCase("help")) { sender.sendMessage(getHelp(args[1])); return true; } 
			if(args[0].equalsIgnoreCase("add")) {
				if(args.length < 3) { sender.sendMessage(TextFormat.RED + "Usage: /user add <PlayerName> <FullName> (<Team Name>)"); return true; }
				if(args.length == 3) { new MinecraftUser(args[1], args[2], Team.defaultTeam);
					sender.sendMessage(TextFormat.GREEN + "A new user has been made!\n"
						+ getUserInfo(args[1], args[2], Team.defaultTeam.getName()));
					return true; 
				}
				new MinecraftUser(args[1], args[2], args[3]); 
				sender.sendMessage(TextFormat.GREEN + "A new user has been made!\n"
						+ getUserInfo(args[1], args[2], args[3]));
				return true;
			}
			if(args[0].equalsIgnoreCase("remove")) {
				if(args.length < 2) { sender.sendMessage(TextFormat.RED + "Usage: /user remove <PlayerName>"); return true; }
				if(UserManager.userMap.get(args[1]) == null) { sender.sendMessage(TextFormat.RED + "There is no user under this name!"); return true; }
				for(int x = 0; x < UserManager.users.size(); x++) {
					if(UserManager.users.get(x).equals(UserManager.userMap.remove(args[1])))
						UserManager.users.remove(x);
				}
				UserManager.userMap.remove(args[1]);
				sender.sendMessage(TextFormat.GREEN + "You successfully removed a player!");
				return true;
			}
			if(args[0].equalsIgnoreCase("edit")) {
				if(args.length < 4) { sender.sendMessage(TextFormat.RED + "Usage: /user edit <username/name/team> <PlayerName> <New Value>"); return true; }
				if(UserManager.userMap.get(args[2]) == null) { sender.sendMessage(TextFormat.RED + "There is not a user with that username!"); return true; }
				if(args[1].equalsIgnoreCase("username")) {
					int index = getIndex(UserManager.userMap.get(args[2]));
					if(UserManager.userMap.get(args[2]) == null || index == -1) { sender.sendMessage(TextFormat.RED + "There is not a user with that username!"); return true; }
					UserManager.users.get(index).editPlayerName(args[3]);
					UserManager.userMap.remove(args[2]);
					UserManager.userMap.put(args[3], UserManager.users.get(index));
					MinecraftUser user = UserManager.userMap.get(args[3]);
					sender.sendMessage(TextFormat.GREEN + "You successfully edited the username of the player!\n"
							+ getUserInfo(user.getPlayerName(), user.getFullName(), user.getTeam().getName()));
					return true;
				}
				if(args[1].equalsIgnoreCase("name")) {
					int index = getIndex(UserManager.userMap.get(args[2]));
					if(UserManager.userMap.get(args[2]) == null || index == -1) { sender.sendMessage(TextFormat.RED + "There is not a user with that username!"); return true; }
					UserManager.users.get(index).editFullName(args[3]);
					UserManager.userMap.remove(args[2]);
					UserManager.userMap.put(args[3], UserManager.users.get(index));
					MinecraftUser user = UserManager.userMap.get(args[3]);
					sender.sendMessage(TextFormat.GREEN + "You successfully edited the name of the player!\n"
							+ getUserInfo(user.getPlayerName(), user.getFullName(), user.getTeam().getName()));
					return true;
				}
				if(args[1].equalsIgnoreCase("team")) {
					int index = getIndex(UserManager.userMap.get(args[2]));
					if(UserManager.userMap.get(args[2]) == null || index == -1) { sender.sendMessage(TextFormat.RED + "There is not a user with that username!"); return true; }
					UserManager.users.get(index).editTeam(TeamManager.checkDefaultClass(args[3]));
					UserManager.userMap.remove(args[2]);
					UserManager.userMap.put(args[2], UserManager.users.get(index));
					MinecraftUser user = UserManager.userMap.get(args[2]);
					sender.sendMessage(TextFormat.GREEN + "You successfully edited the team of the player!\n"
							+ getUserInfo(user.getPlayerName(), user.getFullName(), user.getTeam().getName()));
					return true;
				}
				if(args[1].equalsIgnoreCase("donator")) {
					MinecraftUser user = UserManager.userMap.get(args[2]);
					if(user == null) { sender.sendMessage(TextFormat.RED + "There is not a user with that username!"); return true; }
					if(!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false")) { sender.sendMessage(TextFormat.RED + "Please enter a boolean value! (True / False)"); return true; }
					user.editDonator(Boolean.valueOf(args[3]));
					sender.sendMessage(TextFormat.GREEN + "You succesfully edited the donator status of the player!");
					return true;
				}
				sender.sendMessage(TextFormat.RED + "Usage: /team edit <username/name/team> <PlayerName> <New Value>"); 
				return true;
			}
			if(args[0].equalsIgnoreCase("check")) {
				if(args.length < 1) { sender.sendMessage(TextFormat.RED + "Usage: /user check <PlayerName>"); return true; }
				if(UserManager.userMap.get(args[1]) == null) { sender.sendMessage(TextFormat.RED + "There is not a user with that username!"); return true; }
				MinecraftUser user = UserManager.userMap.get(args[1]);
				sender.sendMessage(TextFormat.GREEN + "Here is the player's information:\n"
						+ getUserInfo(user.getPlayerName(), user.getFullName(), user.getTeam().getName()));
				return true;
			}
			sender.sendMessage(TextFormat.RED + "Unknown subcommand! Usage: /user <subcommand> <arguments>"
					+ TextFormat.RED + "If you need help, please use \"/user help\"");
			return true;
		}
		return false;
	}
	
	private static int getIndex(MinecraftUser user) {
		for(int x = 0; x < UserManager.users.size(); x++) {
			if(UserManager.users.get(x).getPlayerName().equals(user.getPlayerName()))
				return x;
		}
		return -1;
	}
 	private static String getHelp(String str) {
		switch(str) {
		case "default":
			return TextFormat.GREEN + "" + TextFormat.BOLD + "User subcommands: \n"
				+ TextFormat.RESET + TextFormat.GOLD + "help" + TextFormat.RESET + ": Shows information on command and subcommands\n"
				+ TextFormat.RESET + TextFormat.GOLD + "add"  + TextFormat.RESET + ": Creates a new user\n"
				+ TextFormat.RESET + TextFormat.GOLD + "remove" + TextFormat.RESET + ": Removes an existing user\n"
				+ TextFormat.RESET + TextFormat.GOLD + "edit" + TextFormat.RESET + ": Edits the options of an existing user\n"
				+ TextFormat.RESET + TextFormat.GOLD + "check" + TextFormat.RESET + ": Checks the information of a player";
		case "help":
			return TextFormat.GREEN + "Help Command\n"
					+ TextFormat.RESET + "Shows information on subcommands (Things in parenthesis are optional)\n"
					+ TextFormat.RED + "Usage: /user help <subcommand>";
		case "add":
			return TextFormat.GREEN + "Add Command\n"
					+ TextFormat.RESET + "Creates a new user\n"
					+ TextFormat.RED + "Usage: /user add <PlayerName> <FullName> (<Team Name>)";
		case "remove":
			return TextFormat.GREEN + "Removes Command\n"
			+ TextFormat.RESET + "Removes an existing user\n"
			+ TextFormat.RED + "Usage: /user remove <PlayerName>";
		case "edit":
			return TextFormat.GREEN + "Edit Command\n"
			+ TextFormat.RESET + "Edits an existing user\n"
			+ TextFormat.RED + "Usage: /user edit <username/name/team/donator> <PlayerName> <New Value>";
		case "check":
			return TextFormat.GREEN + "Check Command\n"
			+ TextFormat.RESET + "Checks an existing user's information\n"
			+ TextFormat.RED + "Usage: /user check <PlayerName>";
		default:
			return TextFormat.RED + "Unknown subcommand! Please make sure you spelled it correct!\n"
					+ TextFormat.RED + "If you need the list of subcommands, please use \"/user help\"";
		}
		
	}
 	private static String getUserInfo(String userName, String name, String team) {
 		return TextFormat.BLUE + "Username" + TextFormat.RESET + ": " + userName + "\n"
				+ TextFormat.BLUE + "Name" + TextFormat.RESET + ": " + name + "\n"
				+ TextFormat.BLUE + "Team" + TextFormat.RESET + ": " + team + "\n";
 	}
}
