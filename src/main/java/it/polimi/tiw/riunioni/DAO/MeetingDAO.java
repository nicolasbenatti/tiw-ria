package it.polimi.tiw.riunioni.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.riunioni.beans.MeetingBean;

public class MeetingDAO {
	public Connection conn;
	
	public MeetingDAO(Connection dbConnection) {
		this.conn = dbConnection;
	}
	
	public List<MeetingBean> getMeetingsHostedByUser(int userid) throws SQLException {
		String query = "SELECT M.* FROM hostings H NATURAL JOIN meetings M WHERE host_user_id = ? AND M.meeting_date >= current_timestamp()";
		List<MeetingBean> res = new ArrayList<MeetingBean>();
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setInt(1, userid);
			try (ResultSet resSet = pstatement.executeQuery();) {
				while(resSet.next()) {
					MeetingBean toAdd = new MeetingBean();
					toAdd.setId(resSet.getInt("meeting_id"));
					toAdd.setTitle(resSet.getString("title"));
					toAdd.setDate(resSet.getTimestamp("meeting_date"));
					toAdd.setDuration(resSet.getString("duration"));
					toAdd.setMaxParticipants(resSet.getInt("max_participants"));
					
					res.add(toAdd);
				}
			}
		}
		
		return res;
	}
	
	public List<MeetingBean> getMeetingsAttendedByUser(int userid) throws SQLException {
		String query = "SELECT M.* FROM attendances A NATURAL JOIN meetings M WHERE A.attendee = ? AND M.meeting_date >= current_timestamp()";
		List<MeetingBean> res = new ArrayList<MeetingBean>();
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setInt(1, userid);
			try (ResultSet resSet = pstatement.executeQuery();) {
				while(resSet.next()) {
					MeetingBean toAdd = new MeetingBean();
					toAdd.setId(resSet.getInt("meeting_id"));
					toAdd.setTitle(resSet.getString("title"));
					toAdd.setDate(resSet.getTimestamp("meeting_date"));
					toAdd.setDuration(resSet.getString("duration"));
					toAdd.setMaxParticipants(resSet.getInt("max_participants"));
					
					res.add(toAdd);
				}
			}
		}
		
		return res;
	}
	
	public int getIdFromName(String name) throws SQLException {
		String query = "SELECT meeting_id from meetings WHERE title = ?";
		int res = 0;
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setString(1, name);
			try (ResultSet resSet = pstatement.executeQuery();) {
				resSet.next();
				res = resSet.getInt("meeting_id");
			}
		}
		
		return res;
	}
	
	public int createMeeting(String title, Date date, String duration, int maxParticipants) throws SQLException {
		String query = "INSERT INTO meetings(title, meeting_date, duration, max_participants) VALUES(?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
			pstatement.setString(1, title);
			pstatement.setObject(2, new java.sql.Timestamp(date.getTime()));
			pstatement.setString(3, duration);
			pstatement.setInt(4, maxParticipants);
			
			pstatement.executeUpdate();
			
			ResultSet rSet = pstatement.getGeneratedKeys();
			rSet.next();
			return rSet.getInt(1);
		}
	}
	
	/**
	 * adds the user as a host for the specified meeting
	 * @param userId the host user id
	 * @param meetingId the meeting id
	 * @throws SQLException 
	 */
	public void hostMeeting(int userId, int meetingId) throws SQLException {
		String query = "INSERT INTO hostings(host_user_id, meeting_id) VALUES(?, ?)";
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.setInt(2, meetingId);
			
			pstatement.executeUpdate();
		}
	}
	
	public void inviteUserToMeeting(int userId, int meetingId) throws SQLException {
		String query = "INSERT INTO attendances(attendee, meeting_id) VALUES(?, ?)";
		
		try (PreparedStatement pstatement = this.conn.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.setInt(2, meetingId);
			
			pstatement.executeUpdate();
		}
	}
}
