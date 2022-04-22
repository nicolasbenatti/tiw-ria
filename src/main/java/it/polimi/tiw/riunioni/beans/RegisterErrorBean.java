package it.polimi.tiw.riunioni.beans;

public class RegisterErrorBean {
	private String notUniqueUsername = "Username già impiegato";
	private String invalidEmail = "Indirizzo mail malformato";
	private String passwordMismatch = "Le password non corrispondono";
	
	private String getNotUniqueUsername() {
		return this.notUniqueUsername;
	}
	
	private String getInvalidEmail() {
		return this.invalidEmail;
	}
	
	private String getPasswordMismatch() {
		return this.passwordMismatch;
	}
}
