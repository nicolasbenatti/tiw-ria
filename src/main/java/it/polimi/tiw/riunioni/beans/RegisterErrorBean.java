package it.polimi.tiw.riunioni.beans;

public class RegisterErrorBean {
	private String notUniqueUsername;
	private String invalidEmail;
	private String passwordMismatch;
	private String missingEntries;
	
	public RegisterErrorBean() {
		this.notUniqueUsername = "";
		this.invalidEmail ="";
		this.passwordMismatch = "";
		this.missingEntries = "";
	}
	
	public void setNotUniqueUsername(String alert) {
		this.notUniqueUsername = alert;
	}
	
	public void setInvalidEmail(String alert) {
		this.invalidEmail = alert;
	}
	
	public void setPasswordMismatch(String alert) {
		this.passwordMismatch = alert;
	}
	
	public void setMissingEntries(String alert) {
		this.missingEntries = alert;
	}
	
	public String getNotUniqueUsername() {
		return this.notUniqueUsername;
	}
	
	public String getInvalidEmail() {
		return this.invalidEmail;
	}
	
	public String getPasswordMismatch() {
		return this.passwordMismatch;
	}
	
	public String getMissingEntries() {
		return this.missingEntries;
	}
}
