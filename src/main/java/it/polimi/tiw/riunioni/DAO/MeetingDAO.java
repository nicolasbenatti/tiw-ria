package it.polimi.tiw.riunioni.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.Statement;

import it.polimi.tiw.riunioni.beans.MeetingBean;

public class MeetingDAO {
	public Connection conn;
	
	public MeetingDAO(Connection dbConnection) {
		this.conn = dbConnection;
	}
	
	public List<MeetingBean> getMeetingsHostedByUser(int userid) throws SQLException {
		List<MeetingBean> res = new ArrayList<MeetingBean>();
		ResultSet resSet = null;
		String query = "SELECT M.* FROM hostings H NATURAL JOIN meetings M WHERE host_user_id = ?";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setInt(1, userid);
			
			resSet = pstatement.executeQuery();
			while(resSet.next()) {
				MeetingBean toAdd = new MeetingBean();
				toAdd.setTitle(resSet.getString("title"));
				toAdd.setDate(resSet.getTimestamp("meeting_date").getTime());
				toAdd.setDuration(resSet.getInt("duration"));
				toAdd.setMaxParticipants(resSet.getInt("max_participants"));
				
				res.add(toAdd);
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				resSet.close();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				pstatement.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return res;
	}
	
	public List<MeetingBean> getMeetingsAttendedByUser(int userid) throws SQLException {
		List<MeetingBean> res = new ArrayList<MeetingBean>();
		ResultSet resSet = null;
		String query = "SELECT M.* FROM attendances A NATURAL JOIN meetings M WHERE A.attendee = ?";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setInt(1, userid);
			
			resSet = pstatement.executeQuery();
			while(resSet.next()) {
				MeetingBean toAdd = new MeetingBean();
				toAdd.setTitle(resSet.getString("title"));
				toAdd.setDate(resSet.getTimestamp("meeting_date").getTime());
				toAdd.setDuration(resSet.getInt("duration"));
				toAdd.setMaxParticipants(resSet.getInt("max_participants"));
				
				res.add(toAdd);
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				resSet.close();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				pstatement.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return res;
	}
	
	public int getIdFromName(String name) throws SQLException {
		String query = "SELECT meeting_id from meetings WHERE title = ?";
		PreparedStatement pstatement = null;
		ResultSet resSet = null;
		int res = 0;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setString(1, name);
			
			resSet = pstatement.executeQuery();
			resSet.next();
			res = resSet.getInt("meeting_id");
		} catch(SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				resSet.close();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				pstatement.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return res;
	}
	
	public void hostMeeting(MeetingBean meeting) throws SQLException {
		String query = "INSERT INTO meetings(title, meeting_date, duration, max_participants) VALUES(?, ?, ?, ?)";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setString(1, meeting.getTitle());
			pstatement.setObject(2, new java.sql.Date(meeting.getDate()).toLocalDate());
			pstatement.setInt(3, meeting.getDuration());
			pstatement.setInt(4, meeting.getMaxParticipants());
			
			pstatement.executeUpdate();
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void inviteUserToMeeting(int userId, int meetingId) throws SQLException {
		String query = "INSERT INTO attendances(attendee, meeting_id) VALUES(?, ?)";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setInt(2, meetingId);
			
			pstatement.executeUpdate();
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getMaxParticipants(int meetingId) throws SQLException {
		int result;
		ResultSet resSet = null;
		String query = "SELECT max_participants FROM meetings WHERE meeting_id= ?";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setInt(1, meetingId);
			
			resSet = pstatement.executeQuery();
			result = resSet.getInt("max_participants");
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				resSet.close();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				pstatement.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return result;
	}
}
