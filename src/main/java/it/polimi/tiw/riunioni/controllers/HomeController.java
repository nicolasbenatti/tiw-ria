package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import it.polimi.tiw.riunioni.DAO.MeetingDAO;
import it.polimi.tiw.riunioni.beans.MeetingBean;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;

@WebServlet("/home")
public class HomeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection conn;   
    private TemplateEngine templateEngine;
    
    public HomeController() {
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
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		List<MeetingBean> attendedMeetings = null, hostedMeetings = null;
		
		// you must access this page through a login, otherwise an error will be displayed
		if(request.getSession().getAttribute("user") == null) {
			request.getSession().setAttribute("noLogin", true);
			ctx.setVariable("errorNoLogin", "YOU MUST LOGIN BEFORE ACCESSING THIS PAGE");
			this.templateEngine.process(path, ctx, response.getWriter());
			return;
		}

		request.getSession().setAttribute("noLogin", false);
		MeetingDAO meetDao = new MeetingDAO(this.conn);
		try {
			int userId = ((UserBean)request.getSession().getAttribute("user")).getId();
			attendedMeetings = meetDao.getMeetingsAttendedByUser(userId);
			hostedMeetings = meetDao.getMeetingsHostedByUser(userId);
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to retrieve meetings from DB");
		}
		
		ctx.setVariable("hostedMeetings", hostedMeetings);
		ctx.setVariable("meetingsToAttend", attendedMeetings);		
		this.templateEngine.process(path, ctx, response.getWriter());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
