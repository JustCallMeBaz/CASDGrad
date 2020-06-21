package com.casdonline.graduation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import cn.nukkit.Player;

public class StartThread implements Runnable {
	private Main instance;
	private Thread t;
	private String threadName = "start";
	private ArrayList<StartObject> objects;
	private Collection<Player> players;
	
	public static boolean started = false;
	
	public StartThread(Main instance, ArrayList<StartObject> objects, Collection<Player> players) {
		this.instance = instance;
		this.objects = objects;
		this.players = players;
		if(!StartThread.started)
			this.start();
	}

	@Override
	public void run() {
		for(StartObject start : objects) {
			for(Player player : players) {
				player.teleport(start.getPos());
				player.setGamemode(3);
			}
			for(TitleManager title : start.getText()) {
				int time = title.getDuration();
				for(Player player : players) {
					player.sendTitle(title.getTitle(), title.getSubtitle(), 20, time, 20);
				}
				try {
					instance.getServer().getConsoleSender().sendMessage("Ohp");
					TimeUnit.SECONDS.sleep((time / 20) + 3);
				} catch(InterruptedException e) {
					instance.getServer().getConsoleSender().sendMessage("HOW MOMENT");
				}
			}
		}
		for(Player player : players) {
			Team team = UserManager.userMap.get(player.getName()).getTeam();
			player.teleport(team.getSpawn());
			player.setGamemode(team.getGamemode());
		}
		StartThread.started = false;
	}

	public void start() {
		StartThread.started = true;
		if(t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
}
