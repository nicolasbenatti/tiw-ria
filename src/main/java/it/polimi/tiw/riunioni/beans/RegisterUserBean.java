package it.polimi.tiw.riunioni.beans;

public class RegisterUserBean {
	private String username;
	private String email;
	private String password;
	private String passwordConfirmation;
	
	public String getUsername() {
		return this.username;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getPasswordConfirmation() {
		return this.passwordConfirmation;
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
	
	public void setPasswordConfirmation(String password) {
		this.passwordConfirmation = password;
	}
}
