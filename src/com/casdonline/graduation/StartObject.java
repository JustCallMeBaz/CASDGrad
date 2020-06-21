package com.casdonline.graduation;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.level.Position;

public class StartObject {
	
	private Position pos;
	private List<TitleManager> text = new ArrayList<>();
	
	public StartObject(Position pos, List<TitleManager> text) {
		this.pos = pos; this.text = text;
		Start.objects.add(this);
	}
	
	public StartObject(Position pos, String title, String subtitle, int time) {
		this(pos, toList(title, subtitle, time));
	}
	
	private static List<TitleManager> toList(String title, String subtitle, int time) {
		List<TitleManager> text = new ArrayList<>();
		text.add(new TitleManager(title, subtitle, time));
		return text;
	}
	
	public Position getPos() { return this.pos; }
	public List<TitleManager> getText() { return this.text; }
	
}
