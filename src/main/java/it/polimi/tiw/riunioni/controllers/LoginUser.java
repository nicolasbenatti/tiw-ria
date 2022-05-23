package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.RegisterErrorBean;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;
import it.polimi.tiw.riunioni.utils.Utils;

@WebServlet("/login")
@MultipartConfig
public class LoginUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn = null;

	public LoginUser() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username, password;
		String path = "register.html";

		// fill also the signup bean to avoid crashes
		RegisterErrorBean regErrBean = new RegisterErrorBean();

		try {
			username = Utils.sanitizeString(request.getParameter("username"));
			password = request.getParameter("password");
			System.out.println(username + ", " + password);
			if (username == null || username.isEmpty() || password == null || password.isEmpty())
				throw new Exception("Missing or empty credentials");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or empty credentials");
			return;
		}

		UserDAO userDao = new UserDAO(this.conn);
		UserBean authEsit = null;
		try {
			authEsit = userDao.authenticate(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not Possible to check credentials, failed to query DB");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// return an error status code and message
		if (authEsit == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Wrong username or password");
		} else {
			request.getSession().setAttribute("user", authEsit);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(authEsit.getUsername());
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {
		}
	}
}
