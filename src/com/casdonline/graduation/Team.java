package com.casdonline.graduation;

import java.util.ArrayList;

import cn.nukkit.level.Position;

public class Team {

	private String name, prefix;
	private boolean blockPerms;
	private Position coords;
	protected static Team defaultTeam = null;
	private int gamemode;
	
	public Team(String name, boolean blockPerms, String prefix, boolean defaultTeam, Position coords, int gamemode) {
		this.name = name;
		this.blockPerms = blockPerms;
		this.prefix = prefix;
		this.coords = coords;
		this.gamemode = gamemode;
		TeamManager.map.put(this, new ArrayList<String>());
		TeamManager.teamMap.put(name, this);
		TeamManager.list.add(this);
		if(defaultTeam || Team.defaultTeam == null || name.equals(Team.defaultTeam.getName()))
			Team.defaultTeam = this;
	}
	public Team(String name, boolean blockPerms, String prefix, boolean defaultTeam, Position coords) { this(name, blockPerms, prefix, defaultTeam, coords, Team.defaultTeam != null ? Team.defaultTeam.getGamemode() : 0); }
	public Team(String name, boolean blockPerms, String prefix) { this(name, blockPerms, prefix, false, defaultTeam.getSpawn()); }
	public Team(String name, String prefix) { this(name, false, prefix); }
	public Team(String name, boolean blockPerms) { this(name, blockPerms, ""); }
	public Team(String name) { this(name, ""); }
	
	public void editName(String name) {
		TeamManager.teamMap.remove(this.getName());
		TeamManager.teamMap.put(name, this);
		this.name = name;
	}
	public void editPerms(boolean blockPerms) { this.blockPerms = blockPerms; }
	public void editPrefix(String prefix) { this.prefix = prefix; }
	public void editSpawn(Position pos) { this.coords = pos; }
	public static void changeDefault(Team defaultTeam) { 
		TeamManager.teamMap.put(Team.defaultTeam.getName(), Team.defaultTeam);
		if(TeamManager.teamMap.get(defaultTeam.getName()) != null)
			TeamManager.teamMap.remove(defaultTeam.getName());
		Team.defaultTeam = defaultTeam;
	}
	
	public String getName() { return name; }
	public boolean getBlockPerms() { return blockPerms;	}
	public String getPrefix() { return prefix; }
	public Position getSpawn() { return coords; }
	public int getSpawnX() { return (int) this.coords.getX(); }
	public int getSpawnY() { return (int) this.coords.getY(); }
	public int getSpawnZ() { return (int) this.coords.getZ(); }
	public int getGamemode() { return this.gamemode; }
	
	public boolean isEqualTo(Team team) {
		boolean name = (this.name.equals(team.getName())),
		blockPerms = this.blockPerms == team.getBlockPerms(),
		prefix = this.getPrefix().equals(team.getPrefix()),
		
		x = this.getSpawnX() == team.getSpawnX(),
		y = this.getSpawnY() == team.getSpawnY(),
		z = this.getSpawnZ() == team.getSpawnZ(),
		
		spawn = x && y && z,
		gamemode = this.gamemode == team.getGamemode();
		
		return name && blockPerms && prefix && spawn && gamemode;
	}
}
