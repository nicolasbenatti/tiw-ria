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
import it.polimi.tiw.riunioni.beans.MeetingBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;

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
		boolean readyToInsert = false;
		
		// get input data from request
		String title = "", date = "", duration = "", maxParticipants = "";
		try {
			title = request.getParameter("meetingTitle");
			date = request.getParameter("meetingDate");
			duration = request.getParameter("meetingDuration");
			maxParticipants = request.getParameter("maxParticipants");
			
			System.out.println(title+", "+date+", "+duration+", "+maxParticipants);
			
			if(title == null || title.isEmpty() || date == null || date.isEmpty() || duration == null || duration.isEmpty()
					|| maxParticipants == null || maxParticipants.isEmpty()) {
				throw new Exception();
			}
		} catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing parameters");
			return;
		}
		
		List<String> checkedIds = Arrays.asList(request.getParameterValues("users"));
		System.out.println("******");
		for(String sel : checkedIds)
			System.out.println(sel);
		System.out.println("******");
		
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing fields");
			return;
		}
		
		// parse input data
		Date meetingDate = null;
		int meetingDuration = 0, maxGuests = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			meetingDate = (Date)sdf.parse(date);
			meetingDuration = Integer.parseInt(duration);
			maxGuests = Integer.parseInt(maxParticipants);
		} catch(NumberFormatException | ParseException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("malformed parameters");
			return;
		}
		
		int guestDelta = 0;
		// stateful checks
		if(selectedUserIds.size() > maxGuests) {
			guestDelta = selectedUserIds.size() - maxGuests;
			if(attempts == 3) {
				attempts = 0;
				// prompt client to redirect
			} else
				attempts++;
			
			System.out.println("ATTEMPT #" + attempts);
		} else {
			// insert meeting and guests in the DB
			// note: this is a transaction in which both the meeting data and the 
			// guests are inserted atomically, in order not to leave the DB
			// in an inconsistent state
			MeetingDAO meetingDao = new MeetingDAO(this.conn);
			try {
				this.conn.setAutoCommit(false);
				meetingDao.hostMeeting(title, meetingDate, meetingDuration, maxGuests);
				int newMeetingId = meetingDao.getIdFromName(title);
				for(String guestId : checkedIds) {
					meetingDao.inviteUserToMeeting(Integer.parseInt(guestId), newMeetingId);
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
		}
		
		String answer;
		if(!readyToInsert) {
			answer = "Troppi utenti selezionati, eliminarne almeno " + guestDelta;
		} else {
			answer = "ok";
		}
		
		String json = new Gson().toJson(answer);
		response.setContentType("a.pplication/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
