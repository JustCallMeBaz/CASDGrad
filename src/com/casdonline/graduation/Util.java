package com.casdonline.graduation;

public class Util {
	protected static Team OPERATOR;
	protected static Team GRADUATE;
	protected static Team SPECTATOR;
	
	public Util() {
		OPERATOR = TeamManager.teamMap.get("Operators");
		GRADUATE = TeamManager.teamMap.get("Graduates");
		SPECTATOR = TeamManager.teamMap.get("Spectators");
	}
}
