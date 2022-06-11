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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.riunioni.DAO.MeetingDAO;
import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;
import it.polimi.tiw.riunioni.utils.Utils;

@WebServlet("/inviteToMeeting")
@MultipartConfig
public class InviteToMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection conn = null;
	private int attempts;
   
    public InviteToMeeting() {
        super();
    }
	
    public void init() throws ServletException {
    	this.attempts = 0;
    	ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String answer = "";
		
		// get input data from request
		String title = "", dateTime = "", time = "", duration = "", maxParticipants = "", host = "";
		try {
			host = Utils.sanitizeString(request.getParameter("userId"));
			title = Utils.sanitizeString(request.getParameter("meetingTitle"));
			dateTime = Utils.sanitizeString(request.getParameter("meetingDateTime"));
			duration = Utils.sanitizeString(request.getParameter("meetingDuration"));
			maxParticipants = Utils.sanitizeString(request.getParameter("maxParticipants"));
			
			if(title == null || title.isEmpty() || dateTime == null || dateTime.isEmpty() || duration == null || duration.isEmpty()
					|| maxParticipants == null || maxParticipants.isEmpty()) {
				throw new Exception();
			}
		} catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing parameters");
			return;
		}
		
		List<String> checkedIds = Arrays.asList(request.getParameterValues("users"));
		List<Integer> selectedUserIds = new ArrayList<>();
		
		// check that all the IDs are present in the DB
		UserDAO userDao = new UserDAO(this.conn);		
		try {
			for(String idString : checkedIds) {
				int id = Integer.parseInt(idString);
				if(userDao.checkUserid(id)) {
					selectedUserIds.add(id);
				}
			}
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing fields");
			return;
		}
		
		// parse input data
		Date meetingDateTime = null, meetingDuration;
		int maxGuests = 0, hours = 0, minutes = 0;
		try {
			dateTime = dateTime.replaceAll("T", " ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			meetingDateTime = (Date)sdf.parse(dateTime + " " + time);
			
			sdf = new SimpleDateFormat("HH:mm");
			meetingDuration = (Date) sdf.parse(duration);
			hours = meetingDuration.getHours();
			minutes = meetingDuration.getMinutes();
			
			maxGuests = Integer.parseInt(maxParticipants);
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid number of participants");
			e.printStackTrace();
			return;
		} catch(ParseException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid timestamp or duration");
			e.printStackTrace();
			return;
		}
		
		int guestDelta = selectedUserIds.size() - maxGuests;
		// stateful checks
		if(guestDelta > 0) {
			if(attempts == 3) {
				attempts = 0;
				answer = "Too many attempts, the meeting won't be created";
			} else {
				attempts++;
				answer = "Too many users invited, deselect at least " + guestDelta;
			}
		} else {
			// insert meeting, host and guests in the DB
			// note: this is done within a transaction, in order not to leave the DB
			// in an inconsistent state
			MeetingDAO meetingDao = new MeetingDAO(this.conn);
			try {
				this.conn.setAutoCommit(false);
				int newMeetingId = meetingDao.createMeeting(title, meetingDateTime, hours + "h " + minutes + "m", maxGuests);
				
				int hostId = ((UserBean)request.getSession().getAttribute("user")).getId();
				meetingDao.hostMeeting(hostId, newMeetingId);
				
				for(String guestId : checkedIds) {
					meetingDao.inviteUserToMeeting(Integer.parseInt(guestId), newMeetingId);
				}
				this.conn.commit();
			} catch(SQLException e) {
				answer = "couldn't insert meeting in DB";
				try {
					this.conn.rollback();
				} catch(SQLException e1) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't rollback transaction");
					return;
				}
			}
			
			answer = "ok";
		}
		
		String json = new Gson().toJson(answer);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
