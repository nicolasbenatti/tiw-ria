package it.polimi.tiw.riunioni.beans;

import java.sql.Date;
import java.sql.Time;

public class MeetingBean {
	private String title;
	private Date date;
	private Time time;
	private int duration;
	private int maxParticipants;
	
	public MeetingBean() {
		this.title = "";
		this.maxParticipants = 0;
		this.date = null;
		this.time = null;
		this.duration = 0;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public Time getTime() {
		return this.time;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public int getMaxParticipants() {
		return this.maxParticipants;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setTime(Time time) {
		this.time = time;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
}