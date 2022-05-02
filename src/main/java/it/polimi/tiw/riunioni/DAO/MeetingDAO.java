package it.polimi.tiw.riunioni.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

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
				toAdd.setTime(resSet.getTime("meeting_time"));
				toAdd.setDate(resSet.getDate("meeting_date"));
				toAdd.setDuration(resSet.getTime("duration"));
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
				toAdd.setTime(resSet.getTime("meeting_time"));
				toAdd.setDate(resSet.getDate("meeting_date"));
				toAdd.setDuration(resSet.getTime("duration"));
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
	
	public void hostMeeting(MeetingBean meeting) throws SQLException {
		String query = "INSERT INTO meetings(title, meeting_date, meeting_time, duration, max_participants) VALUES(?, ?, ?, ?, ?)";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = this.conn.prepareStatement(query);
			pstatement.setString(1, meeting.getTitle());
			pstatement.setDate(2, meeting.getDate());
			pstatement.setTime(3, meeting.getTime());
			pstatement.setTime(4, meeting.getDuration());
			pstatement.setInt(5, meeting.getMaxParticipants());
			
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
}
