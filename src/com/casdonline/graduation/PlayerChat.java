package com.casdonline.graduation;

public class PlayerChat {
	private String name;
	private Team[] teamVisible;
	private Team[] teamChat;
	public static PlayerChat defaultChat = null;
	
	public PlayerChat(String name, Team[] teamVisible, Team[] teamChat, boolean defaultChat) {
		this.name = name;
		this.teamVisible = teamVisible;
		this.teamChat = teamChat;
		if(defaultChat || PlayerChat.defaultChat == null)
			PlayerChat.defaultChat = this;
	}
	
	public String getName() { return this.name; }
	public Team[] getTeamVisible() { return this.teamVisible; }
	public Team[] getTeamChat() { return this.teamChat; }
	
	public void editName(String name) { this.name = name; }
	public void editVisibleTeams(Team[] teamVisible) { this.teamVisible = teamVisible; }
	public void editChattableTeams(Team[] teamChat) { this.teamChat = teamChat; }
}
