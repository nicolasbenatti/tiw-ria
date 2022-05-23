package it.polimi.tiw.riunioni.beans;

import java.sql.Date;

public class MeetingBean {
	private int id;
	private String title;
	private Date date;
	private int duration;
	private int maxParticipants;
	
	public MeetingBean() {
		this.id = 0;
		this.title = "";
		this.maxParticipants = 0;
		this.date = null;
		this.duration = 0;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public int getMaxParticipants() {
		return this.maxParticipants;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
}