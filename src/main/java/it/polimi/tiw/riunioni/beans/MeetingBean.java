package it.polimi.tiw.riunioni.beans;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class MeetingBean {
	private int id;
	private String title;
	private long date; // milliseconds from 1/1/1970
	private int duration;
	private int maxParticipants;
	
	public MeetingBean() {
		this.id = 0;
		this.title = "";
		this.maxParticipants = 0;
		this.date = 0;
		this.duration = 0;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public long getDate() {
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
	
	public void setDate(long date) {
		this.date = date;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
}