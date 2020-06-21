package com.casdonline.graduation;

import cn.nukkit.Player;

public class MinecraftUser {
	
	private String fullName, playerName;
	private Team team;
	private boolean donator;
	
	public MinecraftUser(String playerName, String fullName, Team team, boolean donator) {
		this.fullName = fullName;
		this.playerName = playerName;
		this.team = team;
		UserManager.users.add(this);
		UserManager.userMap.put(playerName, this);
		if(!TeamManager.map.get(team).contains(playerName))
			TeamManager.map.get(team).add(playerName);
		if(!UserManager.userMap.containsKey(playerName))
			TeamManager.addPlayer(playerName, team);
	}
	public MinecraftUser(String playerName, String fullName, Team team) { this(playerName, fullName, team, false); }
	public MinecraftUser(String playerName, String fullName, String teamName, boolean donator) { this(playerName, fullName, TeamManager.checkDefaultClass(teamName), donator); } 
	public MinecraftUser(String playerName, String fullName, String teamName) { this(playerName, fullName, TeamManager.checkDefaultClass(teamName)); }
	public MinecraftUser(Player player, String fullName, Team team) { this(player.getName(), fullName, team); }	
	public MinecraftUser(Player player, String fullName, String teamName) { this(player.getName(), fullName, TeamManager.checkDefaultClass(teamName)); }
	
	public void editFullName(String fullName) { this.fullName = fullName; }
	public void editPlayerName(String playerName) { 
		UserManager.userMap.remove(this.getPlayerName());
		UserManager.userMap.put(playerName, this);
		this.playerName = playerName; 
	}
	public void editTeam(Team team) { this.team = team; }
	public void editDonator(boolean donator) { this.donator = donator; }
	
	public String getFullName() { return fullName; }
	public String getPlayerName() { return playerName; }
	public Team getTeam() { return team; }
	public boolean getDonator() { return donator; }
	
	public boolean isEqualTo(MinecraftUser user) {
		boolean fullName = this.fullName.equalsIgnoreCase(user.getFullName()),
		playerName = this.playerName.equalsIgnoreCase(user.getFullName()),
		team = this.team.isEqualTo(user.getTeam()),
		donator = this.donator == user.getDonator();
		
		return fullName && playerName && team && donator;
	}
	
}
