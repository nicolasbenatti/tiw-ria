package it.polimi.tiw.riunioni.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.riunioni.beans.UserBean;


public class UserDAO {
	
	public Connection conn;
	
	public UserDAO(Connection dbConn) {
		this.conn = dbConn;
	}
	
	public List<UserBean> getAllUsers() throws SQLException {
		List<UserBean> users = new ArrayList<UserBean>();
		ResultSet res = null;
		Statement statement = null;
		String query = "SELECT * FROM users";
		
		try {
			statement = this.conn.createStatement();
			res = statement.executeQuery(query);
			while(res.next()) {
				UserBean user = new UserBean();
				user.setId(res.getInt("user_id"));
				user.setUsername(res.getString("username"));
				user.setEmail(res.getString("email"));
				user.setPassword(res.getString("user_password"));
				
				users.add(user);
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				res.close();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				statement.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return users;
	}
	
	public int createUser(UserBean user) throws SQLException {
		String query = "INSERT INTO users(username, email, user_password) VALUES(?, ?, ?)";
		PreparedStatement pstatement = null;
		int esit = 0;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setString(1, user.getUsername());
			pstatement.setString(2, user.getEmail());
			pstatement.setString(3, user.getPassword());

			esit = pstatement.executeUpdate();
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return esit;
	}
	
	public boolean isUsernameAlreadyPresent(String username) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ?";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		boolean isResultSetEmpty;
	
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setString(1, username);
			
			res = pstatement.executeQuery();
			isResultSetEmpty = !res.isBeforeFirst();
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				res.close();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				pstatement.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
	
		System.out.println("**" + isResultSetEmpty + "**");
		
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
}
