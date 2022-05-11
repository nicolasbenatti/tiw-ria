package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
	private Connection conn = null;   
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
		String path = "anagrafica.html";
		boolean readyToInsert = false;
		
		// distinguish between the first time you land on the page, and the retries
		List<String> checkedIds = null;
		if(request.getParameterValues("users") == null) {
			System.out.println("first landing");
			checkedIds = new ArrayList<>();
		} else {
			checkedIds = Arrays.asList(request.getParameterValues("users"));
			System.out.println("******");
			for(String sel : checkedIds)
				System.out.println(sel);
			System.out.println("******");
		}
		
		// check that all the IDs are present in the DB
		UserDAO userDao = new UserDAO(this.conn);
		List<Integer> selectedUserIds = new ArrayList<>();
		
		try {
			for(String idString : checkedIds) {
				int id = Integer.parseInt(idString);
				if(userDao.checkUserid(id)) {
					selectedUserIds.add(id);
				}
			}
		} catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "can't check id integrity from DB");
			return;
		}
		
		List<UserBean> users = new ArrayList<>();
		List<SelectedUserBean> selectedUsers = new ArrayList<>();
		try {
			users = userDao.getAllUsersExcept(((UserBean)request.getSession().getAttribute("user")).getId());
			for(UserBean b : users) {
				SelectedUserBean tmp= new SelectedUserBean();
				if(selectedUserIds.contains(b.getId())) {
					System.out.println("OSSIGNUR");
					tmp.setSelected(true);
				}
				else
					tmp.setSelected(false);
				
				tmp.setUserName(b.getUsername());
				tmp.setId(b.getId());
				selectedUsers.add(tmp);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		String title = "", date = "", duration = "", maxParticipants = "";
		try {
			title = request.getParameter("meetingtitle");
			date = request.getParameter("meetingdate");
			duration = request.getParameter("meetingduration");
			maxParticipants = request.getParameter("maxparticipants");
			
			if(title == null || title.isEmpty() || date == null || date.isEmpty() || duration == null || duration.isEmpty()
					|| maxParticipants == null || maxParticipants.isEmpty()) {
				throw new Exception();
			}
		} catch(Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameters");
			return;
		}
		
		long meetingDate = 0;
		int meetingDuration = 0, maxGuests = 0;
		try {
			meetingDate = Long.parseLong(date);
			meetingDuration = Integer.parseInt(duration);
			maxGuests = Integer.parseInt(maxParticipants);
		} catch(NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "malformed parameters");
			return;
		}
		
		MeetingBean newMeeting = new MeetingBean();
		newMeeting.setTitle(title);
		newMeeting.setDate(meetingDate);
		newMeeting.setDuration(meetingDuration);
		newMeeting.setMaxParticipants(maxGuests);
		
		if(selectedUsers.size() > newMeeting.getMaxParticipants()) {
			if(attempts == 3) {
				attempts = 0;
				path = getServletContext().getContextPath() + "/abortMeeting";
				response.sendRedirect(path);
			} else
				attempts++;
			
			System.out.println("ATTEMPT #" + attempts);
			
			path = "anagrafica.html";
		} else {
			if(checkedIds.size() > 0) {
				// insert meeting and guests in the DB
				// note: this is a transaction in which both the meeting data and the 
				// guests are inserted atomically, in order not to leave the DB
				// in an inconsistent state
				MeetingDAO meetingDao = new MeetingDAO(this.conn);
				try {
					this.conn.setAutoCommit(false);
					meetingDao.hostMeeting(newMeeting);
					newMeeting.setId(meetingDao.getIdFromName(newMeeting.getTitle()));
					for(String guestId : checkedIds) {
						meetingDao.inviteUserToMeeting(Integer.parseInt(guestId), newMeeting.getId());
					}
					this.conn.commit();
				} catch(SQLException e) {
					try {
						this.conn.rollback();
					} catch(SQLException e1) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can't rollback transaction");
						return;
					}
				}
				
				readyToInsert = true;
				path = "/goToHome";
			}
		}
		
		if(!readyToInsert) {
			System.out.println("not ready to insert");
			ctx.setVariable("meetingTitle", request.getParameter("meetingtitle"));
			ctx.setVariable("newMeeting", newMeeting);
			ctx.setVariable("selectedUsers", selectedUsers);
			this.templateEngine.process(path, ctx, response.getWriter());
		} else {
			path = getServletContext().getContextPath() + path;
			response.sendRedirect(path);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
