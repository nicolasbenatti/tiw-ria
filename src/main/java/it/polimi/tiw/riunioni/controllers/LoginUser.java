package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.RegisterErrorBean;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;
import it.polimi.tiw.riunioni.utils.SanitizeUtils;

@WebServlet("/login")
public class LoginUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection conn = null;
    private TemplateEngine templateEngine;
    
    public LoginUser() {
    	super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username, password;
		String path = "register.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		// fill also the signup bean to avoid crashes
		RegisterErrorBean regErrBean = new RegisterErrorBean();
		ctx.setVariable("error", regErrBean);
		
		try {
			username = SanitizeUtils.sanitizeString(request.getParameter("username"));
			password = request.getParameter("password");
			System.out.println(username + ", " + password);
			if(username == null || username.isEmpty() || password == null || password.isEmpty())
				throw new Exception("Missing or empty credentials");
		} catch(Exception e) {
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty credentials");
			ctx.setVariable("errorMsg", "Missing or empty credentials");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		UserDAO userDao = new UserDAO(this.conn);
		UserBean authEsit = null;
		try {
			authEsit = userDao.authenticate(username, password);
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check credentials");
			return;
		}
		
		if(authEsit == null) {
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Incorrect username or password");
			ctx.setVariable("errorMsg", "Wrong username or password");
			path = "/register.html";
			this.templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("user", authEsit);
			//request.getSession().setAttribute("noLogin", false);
			path = getServletContext().getContextPath() + "/home.html";
			response.sendRedirect(path);
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
