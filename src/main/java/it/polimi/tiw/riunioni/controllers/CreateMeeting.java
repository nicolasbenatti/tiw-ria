package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;

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

/**
 * Servlet implementation class CreateMeeting
 */
@WebServlet("/createMeeting")
public class CreateMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;   
    private TemplateEngine templateEngine;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
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
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	/*protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}*/

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "home.html";
		ServletContext servletContext = getServletContext(); // contesto in cui lavora il servlet
		final WebContext ctx = new WebContext(request,response, servletContext,request.getLocale()); //oggetto in cui metti i dati che servono al template
		
		String title, date, time,duration, maxParticipants;
		
		
		try{
			title = SanitizeUtils.sanitizeString(request.getParameter("meetingTitle"));
			date = SanitizeUtils.sanitizeString(request.getParameter("meetingDate"));
			time = SanitizeUtils.sanitizeString(request.getParameter("meetingTime"));
			duration = SanitizeUtils.sanitizeString(request.getParameter("meetingDuration"));
			maxParticipants = SanitizeUtils.sanitizeString(request.getParameter("maxParticipants"));
			if(title==null || title.isEmpty() || date==null || date.isEmpty() || time==null || time.isEmpty() 
					|| duration==null || duration.isEmpty() || maxParticipants==null || maxParticipants.isEmpty())
				throw new Exception("Missing fields");
		}catch(Exception e) {
			ctx.setVariable("errorMsg", e.getMessage()); 
			this.templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		
		
	}

}
