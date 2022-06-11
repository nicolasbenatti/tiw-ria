package it.polimi.tiw.riunioni.beans;

import java.sql.Timestamp;

public class MeetingBean {
	private int id;
	private String title;
	private Timestamp date;
	private String duration;
	private int maxParticipants;
	
	public MeetingBean() {
		this.id = 0;
		this.title = "";
		this.maxParticipants = 0;
		this.date = null;
		this.duration = "";
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Timestamp getDate() {
		return this.date;
	}
	
	public String getDuration() {
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
	
	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
}