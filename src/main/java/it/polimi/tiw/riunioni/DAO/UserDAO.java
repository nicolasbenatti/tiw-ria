package it.polimi.tiw.riunioni.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.riunioni.beans.UserBean;

public class UserDAO {
	
	public Connection conn;
	
	public UserDAO(Connection dbConn) {
		this.conn = dbConn;
	}
	
	public List<UserBean> getAllUsersExcept(int userid) throws SQLException {
		String query = "SELECT * FROM users WHERE user_id <> ?";
		List<UserBean> users = new ArrayList<UserBean>();
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setInt(1, userid);
			try (ResultSet resSet = pstatement.executeQuery();) {
				while(resSet.next()) {
					UserBean user = new UserBean();
					user.setId(resSet.getInt("user_id"));
					user.setUsername(resSet.getString("username"));
					user.setEmail(resSet.getString("email"));
					user.setPassword(resSet.getString("user_password"));
					
					users.add(user);
				}
			}
		}
		
		return users;
	}
	
	public int createUser(UserBean user) throws SQLException {
		String query = "INSERT INTO users(username, email, user_password) VALUES(?, ?, ?)";
		int esit = 0;
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setString(1, user.getUsername());
			pstatement.setString(2, user.getEmail());
			pstatement.setString(3, user.getPassword());

			esit = pstatement.executeUpdate();
		}
		
		return esit;
	}
	
	public boolean isUsernameAlreadyPresent(String username) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ?";
		boolean isResultSetEmpty;
	
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet resSet = pstatement.executeQuery();) {
				isResultSetEmpty = !resSet.isBeforeFirst();
			}
		}
	
		return !isResultSetEmpty;
	}
	
	public UserBean authenticate(String username, String password) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ? AND user_password = ?";
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet rset = pstatement.executeQuery();) {
				if(!rset.isBeforeFirst())
					return null;
				
				rset.next();
				
				UserBean user = new UserBean();
				user.setId(rset.getInt("user_id"));
				user.setUsername(rset.getString("username"));
				user.setEmail(rset.getString("email"));
				user.setPassword(rset.getString("user_password"));
				
				return user;
			}
		}
	}
	
	public boolean checkUserid(int userid) throws SQLException {
		String query = "SELECT * FROM users WHERE EXISTS (SELECT * FROM users WHERE user_id = ?)";
		boolean isIdPresent = false;
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setInt(1, userid);
			try (ResultSet resSet = pstatement.executeQuery();) {
				isIdPresent = resSet.isBeforeFirst();
			}
		}
		
		return isIdPresent;
	}
}
