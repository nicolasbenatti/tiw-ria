package it.polimi.tiw.riunioni.beans;

public class SelectedUserBean {
	private String userName;
	private boolean selected;
	
	
	public SelectedUserBean() {
		userName = "";
		selected = false;
	}
	

	public String getUserName() {
		return this.userName;
	}
	
	public boolean getSelected() {
		return this.selected;
	}
	
	public void setUserName(String username) {
		this.userName = username;
	}
	
	public void setSelected(boolean s) {
		this.selected = s;
	}
}
