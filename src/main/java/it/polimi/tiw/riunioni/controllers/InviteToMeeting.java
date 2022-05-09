package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.SelectedUserBean;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;

@WebServlet("/inviteToMeeting")
public class InviteToMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;   
    private TemplateEngine templateEngine;
	private int attempts;
   
    public InviteToMeeting() {
        super();
    }
	
    public void init() throws ServletException {
    	this.attempts = 0;
    	ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "anagrafica.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		String[] selection = request.getParameterValues("users");
		if(selection == null) {
			selection = new String[0];
			System.out.println("absent parameters");
		} else {
			System.out.println("******");
			for(String sel : selection)
				System.out.println(sel);
			System.out.println("******");
		}
		
		List<String> selectedNames = Arrays.asList(selection);
		List<UserBean> users = new ArrayList<>();
		List<SelectedUserBean> selectedUsers = new ArrayList<>();
		UserDAO dao = new UserDAO(this.conn);
		try {
			users = dao.getAllUsers();
			for(UserBean b: users) {
				//System.out.println(b.getUsername());
				SelectedUserBean tmp= new SelectedUserBean();
				if(selectedNames.contains(b.getUsername()))
					tmp.setSelected(true);
				else
					tmp.setSelected(false);
				
				tmp.setUserName(b.getUsername());
				selectedUsers.add(tmp);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		ctx.setVariable("selectedUsers", selectedUsers);
		this.templateEngine.process(path, ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
