package it.polimi.tiw.riunioni.beans;

public class UserBean {
	private int id;
	private String username;
	private String email;
	private String password;
	
	public UserBean() {
		this.id = 0;
		this.username = "";
		this.email = "";
		this.password = "";
	}
	
	public UserBean(String username, String email, String password) {
		this.id = 0;
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
