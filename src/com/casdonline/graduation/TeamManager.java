package com.casdonline.graduation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class TeamManager implements CommandExecutor {

	public static ArrayList<Team> list = new ArrayList<>();
	public static LinkedHashMap<String, Team> teamMap = new LinkedHashMap<>();
	public static LinkedHashMap<Team, List<String>> map = new LinkedHashMap<>();

	public static void addPlayer(String playerName, Team team) {
		MinecraftUser user = UserManager.userMap.get(playerName);
		if(user == null)
			new MinecraftUser(playerName, "", team);
		user.editTeam(team);
		map.get(team).add(playerName);
	}

	public static void addPlayer(Player player, Team team) { TeamManager.addPlayer(player.getDisplayName(), team); }
	
	public static boolean isTeam(Player player, Team team) { return isTeam(player.getName(), team); }
	public static boolean isTeam(String playerName, Team team) { 
		try {
			return TeamManager.getTeam(playerName).isEqualTo(team);
		}
		catch(NullPointerException e) {
			return false;
		}
	}
	
	public static Team getTeam(String playerName) {
		MinecraftUser user = UserManager.userMap.get(playerName);
		if(user == null)
			return null;
		return user.getTeam();
	}

	public static Team getTeam(Player player) { return getTeam(player.getName()); }

	// <haha> ; player
	// <M̷̎̍m̶̈͠M̵̎̾m̸̃͊m̸̂̓M̴̋̈́m̶̛̃M̸̂̈́ṁ̷͉m̴̾̓M̶̃̚m̶̿͘M̵͒͑m̶̿̚m̵̛̂M̴͛̚> (You're a
	// sIMP Maia)

	public static String[] getPlayers(Team team) {
		return (String[]) TeamManager.map.get(team).toArray(new String[TeamManager.map.size()]);
	}

	public static String[] getTeams() {
		ArrayList<String> rtnArr = new ArrayList<>();
		for(Team team : TeamManager.map.keySet())
			rtnArr.add(team.getName());
		return (String[]) rtnArr.toArray(new String[rtnArr.size()]);
	}

	public static Team checkDefaultClass(String teamName) {
		Team team = teamMap.get(teamName);
		if(team == null || team.getName().equals(Team.defaultTeam.getName()))
			team = Team.defaultTeam;
		return team;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("team")) {
			if((sender instanceof Player)
					&& !TeamManager.isTeam((Player) sender, Util.OPERATOR)) {
				sender.sendMessage(TextFormat.DARK_RED + "You do not have permission to use this command");
				return true;
			}
			if(args.length < 1 || (args[0].equalsIgnoreCase("help")) && args.length < 2) {
				sender.sendMessage(getHelp("default"));
				return true;
			}
			if(args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(getHelp(args[1]));
				return true;
			}
			if(args[0].equalsIgnoreCase("add")) {
				if(args.length < 2) {
					sender.sendMessage(getUsage("add"));
					return true;
				}
				if(args.length == 2) {
					new Team(args[1]);
					sender.sendMessage(getTeamInfo(args[1], "false", ""));
					return true;
				}
				if(args.length == 3 && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {
					new Team(args[1], Boolean.valueOf(args[2]));
					sender.sendMessage(getTeamInfo(args[1], args[2], ""));
					return true;
				}
				if(args.length == 3) {
					new Team(args[1], args[2]);
					sender.sendMessage(getTeamInfo(args[1], "false", args[2]));
					return true;
				}
				new Team(args[1], Boolean.valueOf(args[2]), args[3]);
				sender.sendMessage(TextFormat.GREEN + "You successfully made a new team!\n"
						+ getTeamInfo(args[1], args[2], args[3]));
				return true;
			}
			if(args[0].equalsIgnoreCase("join")) {
				if(args.length < 3) {
					sender.sendMessage(getUsage("join"));
					return true;
				}
				Team team = TeamManager.checkDefaultClass(args[1]);
				boolean users = UserManager.userMap.get(args[2]) != null, teams = false;
				for(Player player : Server.getInstance().getOnlinePlayers().values()) {
					if(player.getName().equalsIgnoreCase(args[2]))
						teams = true;
				}
				if(users && teams) {
					sender.sendMessage(TextFormat.RED + "There is no user with this name!");
					return true;
				}
				TeamManager.addPlayer(args[2], team);
				sender.sendMessage(
						TextFormat.GREEN + "You successfully made " + args[3] + "join the " + team.getName() + " team");
				return true;
			}
			if(args[0].equalsIgnoreCase("remove")) {
				if(args.length < 2) {
					sender.sendMessage(getUsage("join"));
					return true;
				}
				Team team = TeamManager.teamMap.get(args[1]);
				if(team == null) {
					sender.sendMessage("There is no team by this name!");
					return true;
				}
				TeamManager.teamMap.remove(team.getName());
				TeamManager.map.remove(team);
				sender.sendMessage("You have successfully deleted a team!");
				return true;
			}
			// TODO reset
			if(args[0].equalsIgnoreCase("edit")) {
				if(args.length < 4) {
					sender.sendMessage(getUsage("edit"));
					return true;
				}
				Team team = TeamManager.teamMap.get(args[2]);
				if(team == null) {
					sender.sendMessage(TextFormat.RED + "There is not a team by that name!");
					return true;
				}
				if(args[1].equalsIgnoreCase("name")) {
					TeamManager.teamMap.remove(args[3]);
					team.editName(args[3]);
					TeamManager.teamMap.put(args[3], team);
					sender.sendMessage(
							TextFormat.GREEN + "You have successfully edited the name of a team\n" + getTeamInfo(team));
					return true;
				}
				if(args[1].equalsIgnoreCase("perms")) {
					if(!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false")) {
						sender.sendMessage(TextFormat.RED + "");
					}
					team.editPerms(Boolean.valueOf(args[3]));
					sender.sendMessage(TextFormat.GREEN + "You have successfully edited the permissions of a team\n"
							+ getTeamInfo(team));
					return true;
				}
				if(args[1].equalsIgnoreCase("prefix")) {
					team.editPrefix(args[3]);
					sender.sendMessage(TextFormat.GREEN + "You have successfuly edited the prefix of a team\n"
							+ getTeamInfo(team));
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("check")) {
				if(args.length < 2) {
					sender.sendMessage(getUsage("check"));
					return true;
				}
				if(TeamManager.teamMap.get(args[1]) == null) {
					sender.sendMessage(TextFormat.RED + "There is no team with this name!");
					return true;
				}
				sender.sendMessage(getTeamInfo(TeamManager.teamMap.get(args[1])));
				return true;
			}
			if(args[0].equalsIgnoreCase("list")) {
				String str = "";
				for(String string : TeamManager.getTeams()) {
					str += TextFormat.BLUE + string + TextFormat.RESET + ", ";
				}
				str = str.substring(0, str.length() - 2);
				sender.sendMessage(TextFormat.GREEN + "A list of every team name" + TextFormat.RESET + ":\n" + str);
				return true;
			}
			sender.sendMessage(getUsage("default"));
			return true;
		}
		return false;
	}

	private static String getHelp(String str) {
		switch(str) {
		case "default":
			return TextFormat.GREEN + "" + TextFormat.BOLD + "Team subcommands: \n" + TextFormat.RESET + TextFormat.GOLD
					+ "help" + TextFormat.RESET + ": Shows information on command and subcommands\n" + TextFormat.RESET
					+ TextFormat.GOLD + "add" + TextFormat.RESET + ": Creates a new team\n" + TextFormat.RESET
					+ TextFormat.GOLD + "join" + TextFormat.RESET + ": Puts a player onto a team\n" + TextFormat.RESET
					+ TextFormat.GOLD + "remove" + TextFormat.RESET + ": Removes an existing team\n" + TextFormat.RESET
					+ TextFormat.GOLD + "reset" + TextFormat.RESET + ": Sets a player to the default team\n"
					+ TextFormat.RESET + TextFormat.GOLD + "edit" + TextFormat.RESET
					+ ": Edits the options of an existing team\n" + TextFormat.RESET + TextFormat.GOLD + "check"
					+ TextFormat.RESET + ": Checks the team of a player";
		case "help":
			return TextFormat.GREEN + "Help Command\n" + TextFormat.RESET
					+ "Shows information on subcommands (Things in parenthesis are optional)\n" + TextFormat.RED
					+ "Usage: /team help <subcommand>";
		case "add":
			return TextFormat.GREEN + "Add Command\n" + TextFormat.RESET + "Creates a new team\n" + TextFormat.RED
					+ "Usage: /team add <Team Name> (Able to break blocks [true/false]) (Prefix)";
		case "join":
			return TextFormat.GREEN + "Join Command\n" + TextFormat.RESET + "Puts a player onto an existing team\n"
					+ TextFormat.RED + "Usage: /team join <Team Name> <Player Name>";
		case "remove":
			return TextFormat.GREEN + "Remove Command\n" + TextFormat.RESET + "Removes an existing team\n"
					+ TextFormat.RED + "Usage: /team remove <Team Name>";
		case "reset":
			return TextFormat.GREEN + "Reset Command\n" + TextFormat.RESET + "Sets a player to the default team\n"
					+ TextFormat.RED + "Usage: /team reset <Player Name>";
		case "edit":
			return TextFormat.GREEN + "Edit Command\n" + TextFormat.RESET + "Edits an existing team's settings\n"
					+ TextFormat.RED + "Usage: /team edit [name/perms/prefix] <Team Name> <New Value>";
		case "check":
			return TextFormat.GREEN + "Check Command\n" + TextFormat.RESET + "Checks the current team of a player\n"
					+ TextFormat.RED + "Usage: /team check <Player Name>";
		case "list":
			return TextFormat.GREEN + "List Command\n" + TextFormat.RESET + "Lists the names of the teams\n"
					+ TextFormat.RED + "Usage: /team list";
		default:
			return TextFormat.RED + "Unknown subcommand! Make sure you spelled it correctly!\n"
					+ "Please use \"/team help\" for the list of subcommands!";
		}
	}

	private static String getUsage(String str) {
		switch(str) {
		case "default":
			return TextFormat.RED + "Usage: /team <subcommand> <arguments if needed>";
		case "add":
			return TextFormat.RED + "Usage: /team add <Team Name> (Ability to break blocks [true/false]) (Prefix)";
		case "join":
			return TextFormat.RED + "Usage: /team join <Player Name> <Team Name>";
		case "remove":
			return TextFormat.RED + "Usage: /team remove <Team Name>";
		case "reset":
			return TextFormat.RED + "Usage: /team reset <Player Name>";
		case "edit":
			return TextFormat.RED + "Usage: /team edit [name/perms/prefix] <Team Name> <New Value>";
		case "check":
			return TextFormat.RED + "Usage: /team check <Team Name>";
		case "editBool":
			return TextFormat.RED + "Usage: /team edit perms <Team Name> [true/false]";
		default:
			return TextFormat.RED + "Usage: /team <subcommand>";
		}
	}

	private static String getTeamInfo(String teamName, String blockPerms, String prefix) {
		return TextFormat.BLUE + "Team Name:" + TextFormat.RESET + ": " + teamName + "\n" + TextFormat.BLUE
				+ "Able to Break and Place Blocks:" + TextFormat.RESET + ": " + blockPerms + "\n" + TextFormat.BLUE
				+ "prefix" + TextFormat.RESET + ": " + prefix + "[Example]\n";
	}

	private static String getTeamInfo(Team team) {
		return TextFormat.BLUE + "Team Name:" + TextFormat.RESET + ": " + team.getName() + "\n" + TextFormat.BLUE
				+ "Able to Break and Place Blocks:" + TextFormat.RESET + ": " + team.getBlockPerms() + "\n"
				+ TextFormat.BLUE + "prefix" + TextFormat.RESET + ": " + team.getPrefix() + "[Example]\n";
	}

}