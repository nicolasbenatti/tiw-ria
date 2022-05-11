package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import it.polimi.tiw.riunioni.utils.ConnectionHandler;
import it.polimi.tiw.riunioni.utils.SanitizeUtils;

@WebServlet("/createMeeting")
public class CreateMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn = null;   
    private TemplateEngine templateEngine;
    
    public CreateMeeting() {
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
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "home.html";
		ServletContext servletContext = getServletContext(); // contesto in cui lavora il servlet
		final WebContext ctx = new WebContext(request,response, servletContext,request.getLocale()); //oggetto in cui metti i dati che servono al template
		
		String title, date, time, duration, maxParticipants;
		
		try{
			title = SanitizeUtils.sanitizeString(request.getParameter("meetingTitle"));
			date = SanitizeUtils.sanitizeString(request.getParameter("meetingDate"));
			time = SanitizeUtils.sanitizeString(request.getParameter("meetingTime"));
			duration = SanitizeUtils.sanitizeString(request.getParameter("meetingDuration"));
			maxParticipants = SanitizeUtils.sanitizeString(request.getParameter("maxParticipants"));
			if(title == null || title.isEmpty() || date == null || date.isEmpty() || duration == null || duration.isEmpty() || time == null || time.isEmpty()
					|| maxParticipants == null || maxParticipants.isEmpty())
				throw new Exception("Missing fields");
		}catch(Exception e) {
			if(request.getSession().getAttribute("user") == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameters");
			} else {
				ctx.setVariable("errorMsg", e.getMessage()); 
				this.templateEngine.process(path, ctx, response.getWriter());
			}
			return;
		}
		
		System.out.println("FORM FIELDS:");
		System.out.println(title);
		System.out.println(date);
		System.out.println(duration);
		System.out.println(maxParticipants);
		
		Date meetingDate;
		int maxP = 0, meetingDuration = 0;
		
		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			meetingDate = (Date) sdf.parse(date + " " + time);
			meetingDuration = 90;
			maxP = Integer.parseInt(maxParticipants);
		} catch(NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number of participants");
			e.printStackTrace();
			return;
		} catch(IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid hour, time or duration");
			e.printStackTrace();
			return;
		} catch(ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parse error");
			e.printStackTrace();
			return;
		}
		
		if(maxP <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You can't create a meeting with a no. of participants <= 0");
			return;
		}
		
		path = getServletContext().getContextPath() + "/inviteToMeeting";
		response.sendRedirect(path + "?meetingtitle=" + title + "&meetingduration=" + meetingDuration
				+ "&meetingdate=" + meetingDate.getTime() + "&maxparticipants=" + maxP);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
