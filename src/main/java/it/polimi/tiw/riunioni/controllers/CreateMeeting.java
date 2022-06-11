package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.riunioni.utils.Utils;

@WebServlet("/createMeeting")
@MultipartConfig
public class CreateMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public CreateMeeting() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title, date, time, duration, maxParticipants;
		
		// parse meeting data
		try{
			title = Utils.sanitizeString(request.getParameter("meetingTitle"));
			date = Utils.sanitizeString(request.getParameter("meetingDate"));
			time = Utils.sanitizeString(request.getParameter("meetingTime"));
			duration = Utils.sanitizeString(request.getParameter("meetingDuration"));
			maxParticipants = Utils.sanitizeString(request.getParameter("maxParticipants"));
			
			if(title == null || title.isEmpty() || date == null || date.isEmpty() || duration == null || duration.isEmpty() || time == null || time.isEmpty()
					|| maxParticipants == null || maxParticipants.isEmpty())
				throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing fields");
			return;
		}
		
		System.out.println("FORM FIELDS:");
		System.out.println(title);
		System.out.println(date);
		System.out.println(time);
		System.out.println(duration);
		System.out.println(maxParticipants);
		
		Date meetingDate = null, meetingDuration = null;
		int maxP = 0;
		
		// validate meeting data
		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			meetingDate = (Date) sdf.parse(date + " " + time);
			
			sdf = new SimpleDateFormat("HH:mm");
			// throws an exception if the time is invalid
			sdf.parse(duration);
			
			maxP = Integer.parseInt(maxParticipants);
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid number of participants");
			e.printStackTrace();
			return;
		} catch(ParseException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parse error");
			e.printStackTrace();
			return;
		}
		
		if(meetingDate.before(new Date())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("You can't host a meeting which starts earlier than now");
			return;
		}
		
		if(maxP <= 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("You can't host a meeting with a negative number of participants");
			return;
		}
		
		String json = new Gson().toJson("ok");
				
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}
