package com.casdonline.graduation;

public class TitleManager {
	
	private String title, subtitle;
	private int duration;
	
	public TitleManager(String title, String subtitle, int duration) {
		this.title = title; this.subtitle = subtitle; this.duration = duration;
	}
	
	public String getTitle() { return title; }
	public String getSubtitle() { return subtitle; }
	public int getDuration() { return duration; }
}
