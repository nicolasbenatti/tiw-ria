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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.riunioni.DAO.MeetingDAO;
import it.polimi.tiw.riunioni.beans.MeetingBean;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;

@WebServlet("/getAttendedMeetings")
public class GetAttendedMeetings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    private Connection conn;
    
    public GetAttendedMeetings() {
        super();
    }

    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<MeetingBean> attendedMeetings = null; 
		
		// fetch data
		MeetingDAO meetDao = new MeetingDAO(this.conn);
		try {
			int userId = ((UserBean)request.getSession().getAttribute("user")).getId();
			attendedMeetings = meetDao.getMeetingsAttendedByUser(userId);
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Couldn't retrieve meetings from DB");
			return;
		}
		
		// prepare response
		String json = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm").create().toJson(attendedMeetings);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(json);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.conn);
		} catch (SQLException sqle) {}
	}
}
