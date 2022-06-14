package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.SignupErrorBean;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;
import it.polimi.tiw.riunioni.utils.Utils;

@WebServlet("/register")
@MultipartConfig
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String REGEX_VALIDATE_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	private Connection conn = null;
	
	public RegisterUser() {
		super();
	}
	
    public void init() throws ServletException {
		ServletContext context = getServletContext();
		this.conn = ConnectionHandler.getConnection(context);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SignupErrorBean errorBean = new SignupErrorBean();

		String username = "", email = "", password = "", confirmation = "";
		try {
			username = Utils.sanitizeString(request.getParameter("username"));
			email = Utils.sanitizeString(request.getParameter("email"));
			password = request.getParameter("password");
			confirmation = Utils.sanitizeString(request.getParameter("passwordConfirmation"));
			
			if(username == null || username.isEmpty() || email == null || email.isEmpty() || password == null 
					|| password.isEmpty() || confirmation == null || confirmation.isEmpty()) {
				throw new Exception();
			}
		} catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing fields");
			return;
		}
		
		// check for errors
		UserDAO userDao = new UserDAO(this.conn);
		boolean isUsernameDuplicate = false, errors = false;
		
		try {
			isUsernameDuplicate = userDao.isUsernameAlreadyPresent(username);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if(isUsernameDuplicate) {
			errors = true;
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errorBean.setNotUniqueUsername("this username already exists");
		}
		
		if(!Pattern.compile(REGEX_VALIDATE_EMAIL).matcher(email).matches()) {
			errors = true;
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errorBean.setInvalidEmail("invalid email address");
		}
		
		if(password.length() < 8) {
			errors = true;
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errorBean.setPasswordMismatch("password missing or too short (at least 8 characters)");
		} else {
			if(!password.equals(confirmation)) {
				errors = true;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				errorBean.setPasswordMismatch("passwords don't match");
			}
		}
		
		if(errors) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			String json = new Gson().toJson(errorBean);
			response.getWriter().println(json);
			return;
		}
		
		// insert the new user in the DB
		UserBean newUser = new UserBean(username, email, password);
		int created = 0;
		
		try {
			created = userDao.createUser(newUser);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue creating a new user");
			return;
		}
		if(created == 0) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't create a new user");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println("Registration successful, you can now login");
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
