package it.polimi.tiw.riunioni.beans;

public class SelectedUserBean {
	private int id;
	private String userName;
	private boolean selected;
	
	public SelectedUserBean() {
		id = 0;
		userName = "";
		selected = false;
	}
	
	public int getId() {
		return this.id;
	}

	public String getUserName() {
		return this.userName;
	}
	
	public boolean getSelected() {
		return this.selected;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUserName(String username) {
		this.userName = username;
	}
	
	public void setSelected(boolean s) {
		this.selected = s;
	}
}
