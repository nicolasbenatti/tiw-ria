package it.polimi.tiw.riunioni.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.riunioni.DAO.UserDAO;
import it.polimi.tiw.riunioni.beans.UserBean;
import it.polimi.tiw.riunioni.utils.ConnectionHandler;

/**
 * Servlet implementation class GetUsers
 */
@WebServlet("/getUsers")
public class GetUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn = null;
   
    public GetUsers() {
        super();
    }
	
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check that all the IDs are present in the DB
		UserDAO userDao = new UserDAO(this.conn);
		
		List<UserBean> users = new ArrayList<>();
		try {
			users = userDao.getAllUsersExcept(((UserBean)request.getSession().getAttribute("user")).getId());
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		String json = new Gson().toJson(users);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(json);
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
