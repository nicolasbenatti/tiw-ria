package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.MeetingBean;
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
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		// distinguish between the first time you land on the page, and the retries
		String[] selection = request.getParameterValues("users");
		if(selection == null) {
			selection = new String[0];
			System.out.println("no user selected");
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
		
		MeetingBean newMeeting = new MeetingBean();
		newMeeting.setTitle(request.getParameter("meetingtitle"));
		newMeeting.setMaxParticipants(Integer.parseInt(request.getParameter("maxparticipants")));
		
		int maxP =Integer.parseInt(request.getParameter("maxparticipants"));
		
		if(selectedUsers.size() > maxP) {
			if(attempts == 3) {
				attempts = 0;
				String path = getServletContext().getContextPath() + "/cancellazione.html";
				response.sendRedirect(path);
			} else
				attempts++;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date meetingDate = null;
		Date meetingTime = null;
		Date meetingDuration = null;
		
		/*try {
			meetingDate = sdf.parse(request.getParameter("meetingdate"));
			meetingTime = sdf.parse(request.getParameter("meetingtime"));
			meetingDuration = sdf.parse(request.getParameter("meetingduration"));
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}*/
		ctx.setVariable("meetingTitle", request.getParameter("meetingtitle"));
		ctx.setVariable("newMeeting", newMeeting);
		ctx.setVariable("selectedUsers", selectedUsers);
		this.templateEngine.process("anagrafica.html", ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
