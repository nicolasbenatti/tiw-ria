package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;

@WebServlet("/register")
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String REGEX_VALIDATE_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	private Connection conn = null;
	private TemplateEngine templateEngine;
	
	public RegisterUser() {
		super();
	}
	
    public void init() throws ServletException {
		ServletContext context = getServletContext();
		this.conn = ConnectionHandler.getConnection(context);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirmation = request.getParameter("passwordConfirmation");
		
		if(username == null || username.isEmpty() || email == null || email.isEmpty() || password == null 
				|| password.isEmpty() || confirmation == null || confirmation.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameters");
			return;
		}
		
		if(!Pattern.compile(REGEX_VALIDATE_EMAIL).matcher(email).matches()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid email address");
			return;
		}
		
		if(!password.equals(confirmation)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "passwords don't match");
			return;
		}
		
		UserDAO userDao = new UserDAO(this.conn);
		boolean isUsernameDuplicate = false;
		
		try {
			isUsernameDuplicate = userDao.isUsernameAlreadyPresent(username);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if(isUsernameDuplicate) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "the username already exists");
			return;
		}
		
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
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "couldn't create a new user");
			return;
		}
		
		//response.sendRedirect("home.jsp");
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
