package com.casdonline.graduation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;

@SuppressWarnings("unchecked") // Have to do this cuz Nukkit's terrible :)
public class Main extends PluginBase implements Listener {

	Config config, userConfig, teamListConfig, startConfig;

	@Override
	public void onEnable() {
		toConsole(TextFormat.GREEN + "CASD Graduation plugin is staring up!");
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new ChatManager(), this);
		this.getServer().getPluginManager().registerEvents(new ChairEvent(), this);

		// Config shit
		config = new Config(this.getDataFolder() + "/teams.yml", Config.YAML);
		userConfig = new Config(this.getDataFolder() + "/users.yml", Config.YAML);
		teamListConfig = new Config(this.getDataFolder() + "/teamList.yml", Config.YAML);
		startConfig = new Config(this.getDataFolder() + "/startConfig.yml", Config.YAML);

		// I fucking hate Nukkit
		((PluginCommand<Plugin>) this.getCommand("team")).setExecutor(new TeamManager());
		((PluginCommand<Plugin>) this.getCommand("user")).setExecutor(new UserManager());
		((PluginCommand<Plugin>) this.getCommand("setspawn")).setExecutor(new SetSpawn(this));
		((PluginCommand<Plugin>) this.getCommand("display")).setExecutor(new Display(this));
		((PluginCommand<Plugin>) this.getCommand("start")).setExecutor(new Start(this));
		configStartupShit();
		new Util();
	}

	@Override
	public void onDisable() {
		toConsole(TextFormat.DARK_RED + "CASD Graduation plugin is shutting down");
		configShutdownShit();
	}

	@EventHandler
	public void onLogIn(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String name = player.getDisplayName();
		MinecraftUser user = UserManager.userMap.get(name);
		if(user == null) {
			TeamManager.addPlayer(name, TeamManager.teamMap.get("Spectators"));
		} else if(user.getDonator() && user.getTeam() != null
				&& user.getTeam().equals(TeamManager.teamMap.get("Graduates"))) {
			Donator.givePlayerItems(player);
			user.editDonator(false);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		String name = player.getDisplayName();
		if(UserManager.userMap.get(name) == null || !UserManager.userMap.get(name).getTeam().getBlockPerms()) {
			event.setCancelled(true);
			player.sendMessage(TextFormat.DARK_RED + "You do not have permission to break blocks!");
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String name = player.getDisplayName();
		if(UserManager.userMap.get(name) == null || !UserManager.userMap.get(name).getTeam().getBlockPerms()) {
			event.setCancelled(true);
			player.sendMessage(TextFormat.DARK_RED + "You do not have permission to place blocks!");
		}
	}

	@EventHandler
	public void onPlayerMoveWhilstStartCommand(PlayerMoveEvent event) {
		if(StartThread.started)
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodTick(PlayerFoodLevelChangeEvent event) {
		if(event.getPlayer().getFoodData().getLevel() != 20)
			event.getPlayer().getFoodData().setLevel(20);
		event.setCancelled(true);
	}

	private void configStartupShit() {
		Position pos = this.getServer().getDefaultLevel().getSpawnLocation();
		if(!config.exists("teams")) {
			new Team("Spectators", false, "", true, pos);
			new Team("Operators", true, TextFormat.GOLD + "", false, pos);
			new Team("Graduates", false, TextFormat.BOLD + "" + TextFormat.GREEN, false, pos);
			saveTeamConfig();
		} else {
			config.getSection("teams").forEach((key, value) -> {
				String name = key, prefix = config.getString("teams." + key + ".prefix");
				boolean blockPerms = config.getBoolean("teams." + key + ".blockPermissions");
				String temp = "teams." + key + ".spawn.";
				int x = config.getInt(temp + "X"), y = config.getInt(temp + "Y"), z = config.getInt(temp + "Z");
				new Team(name, blockPerms, prefix, false, new Position(x, y, z));
			});
		}
		if(!userConfig.exists("users")) {
			new MinecraftUser("CallMeBaz9379", "Brayden Zimmerman", TeamManager.teamMap.get("Operators"));
			saveUserConfig();
		} else {
			userConfig.getSection("users").forEach((key, value) -> {
				String playerName = key, fullName = userConfig.getString("users." + key + ".name"),
						teamName = userConfig.getString("users." + key + ".team");
				boolean donator = userConfig.getBoolean("users." + key + ".donator");
				new MinecraftUser(playerName, fullName, teamName, donator);
			});
		}
		if(!teamListConfig.exists("teamList")) {
			saveTeamListConfig();
		} else {
			teamListConfig.getSection("teamList").forEach((key, value) -> {
				if(TeamManager.teamMap.get(key) == null) {
					this.getServer().getConsoleSender().sendMessage(TextFormat.RED + "Unknown Team Name! " + key + "\n"
							+ TextFormat.RED + "Making a new team with default parameters!");
					new Team(key, Team.defaultTeam.getBlockPerms(), Team.defaultTeam.getPrefix());
				}
				Team team = TeamManager.teamMap.get(key);
				List<String> list = teamListConfig.getStringList("teamList." + key);
				TeamManager.map.remove(team);
				TeamManager.map.put(team, list);
				for(String str : list) {
					if(UserManager.userMap.get(str) == null) {
						new MinecraftUser(str, "", team);
					} else if(UserManager.userMap.get(str) != null
							&& !UserManager.userMap.get(str).getTeam().getName().equals(team.getName())) {
						UserManager.userMap.get(str).editTeam(team);
					}
				}
			});
		}
		if(!startConfig.exists("start")) {
			new StartObject(this.getServer().getDefaultLevel().getSpawnLocation(), TextFormat.GREEN + "Test",
					TextFormat.RED + "Test2", 50);
			new StartObject(this.getServer().getDefaultLevel().getSpawnLocation(), TextFormat.GREEN + "Test",
					TextFormat.RED + "Test2", 50);
			saveStartConfig();
		} else {
			startConfig.getSection("start").getKeys(false).forEach(num -> {
				int x = startConfig.getInt("start." + num + ".pos.X");
				int y = startConfig.getInt("start." + num + ".pos.Y");
				int z = startConfig.getInt("start." + num + ".pos.Z");
				List<TitleManager> list = new ArrayList<>();
				startConfig.getSection("start." + num + ".titles").getKeys(false).forEach(numTwo -> {
					String title = startConfig.getString("start." + num + ".titles." + numTwo + ".title");
					String subtitle = startConfig.getString("start." + num + ".titles." + numTwo + ".subtitle");
					int duration = startConfig.getInt("start." + num + ".titles." + numTwo + ".duration");
					list.add(new TitleManager(title, subtitle, duration));
				});
				new StartObject(new Position(x, y, z), list);
			});
		}
	}

	private void configShutdownShit() {
		saveTeamConfig();
		saveUserConfig();
		saveTeamListConfig();
		saveStartConfig();
	}

	private void saveTeamConfig() {
		LinkedHashMap<String, Object> teams = new LinkedHashMap<>();
		for(Team team : TeamManager.map.keySet()) {
			LinkedHashMap<String, Object> options = new LinkedHashMap<>();
			LinkedHashMap<String, Object> spawn = new LinkedHashMap<>();
			spawn.put("X", team.getSpawnX());
			spawn.put("Y", team.getSpawnY());
			spawn.put("Z", team.getSpawnZ());
			options.put("blockPermissions", team.getBlockPerms());
			options.put("prefix", team.getPrefix());
			options.put("spawn", spawn);
			teams.put(team.getName(), options);
		}
		ConfigSection section = new ConfigSection("teams", teams);
		config.setAll(section);
		config.save();
	}

	private void saveUserConfig() {
		LinkedHashMap<String, Object> teams = new LinkedHashMap<>();
		for(MinecraftUser user : UserManager.users) {
			LinkedHashMap<String, Object> options = new LinkedHashMap<>();
			options.put("name", user.getFullName());
			options.put("team", user.getTeam().getName());
			options.put("donator", user.getDonator());
			teams.put(user.getPlayerName(), options);
		}
		ConfigSection section = new ConfigSection("teams", teams);
		userConfig.setAll(section);
		userConfig.save();
	}

	private void saveTeamListConfig() {
		LinkedHashMap<String, Object> teams = new LinkedHashMap<>();
		for(Team team : TeamManager.map.keySet())
			teams.put(team.getName(), TeamManager.map.get(team));
		ConfigSection section = new ConfigSection("teamList", teams);
		teamListConfig.setAll(section);
		teamListConfig.save();
	}

	private void saveStartConfig() {
		LinkedHashMap<String, Object> startMap = new LinkedHashMap<>();
		for(int x = 0; x < Start.objects.size(); x++) {
			LinkedHashMap<String, Object> greaterMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> positionMap = new LinkedHashMap<>();
			StartObject object = Start.objects.get(x);
			positionMap.put("X", object.getPos().getX());
			positionMap.put("Y", object.getPos().getY());
			positionMap.put("Z", object.getPos().getZ());
			greaterMap.put("pos", positionMap);
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			for(int y = 0; y < object.getText().size(); y++) {
				TitleManager titleManager = object.getText().get(y);
				LinkedHashMap<String, Object> titleMap = new LinkedHashMap<>();
				String title = titleManager.getTitle(), subtitle = titleManager.getSubtitle();
				int duration = titleManager.getDuration();
				titleMap.put("title", title);
				titleMap.put("subtitle", subtitle);
				titleMap.put("duration", duration);
				map.put(Integer.toString(y), titleMap);
			}
			greaterMap.put("titles", map);
			startMap.put(Integer.toString(x), greaterMap);
		}
		ConfigSection section = new ConfigSection("start", startMap);
		startConfig.setAll(section);
		startConfig.save();
	}

	public void toConsole(String str) { this.getServer().getConsoleSender().sendMessage(str); }
}
