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
		String path = "home.html";
		
		List<MeetingBean> attendedMeetings = null; 
		
		// you must access this page through a login, otherwise an error will be displayed
		if(request.getSession().getAttribute("user") == null) {
			request.getSession().setAttribute("noLogin", true);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "YOU MUST LOGIN BEFORE ACCESSING THIS PAGE");
			return;
		}

		request.getSession().setAttribute("noLogin", false);
		MeetingDAO meetDao = new MeetingDAO(this.conn);
		try {
			int userId = ((UserBean)request.getSession().getAttribute("user")).getId();
			attendedMeetings = meetDao.getMeetingsAttendedByUser(userId);
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to retrieve meetings from DB");
		}
		
		String json = new Gson().toJson(attendedMeetings);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(json);
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
