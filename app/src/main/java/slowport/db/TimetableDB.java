package slowport.db;

import java.sql.*;

import java.util.*;

public class TimetableDB{
	private static final String queryGetVersions =
"""
SELECT DISTINCT version FROM timetables;
""";

	private static final String queryGetTimetable =
"""
SELECT timetable FROM timetables WHERE version=?;
""";

	private static final String queryAddTimetable =
"""
INSERT INTO timetables (version, timetable) VALUES (?, ?);
""";

	private PreparedStatement stmntGetVersions, stmntGetTimetable,
					stmntAddTimetable;

	public TimetableDB(Connection conn) throws DBException{
		try{
			stmntGetVersions = conn.prepareStatement(queryGetVersions);
			stmntGetTimetable = conn.prepareStatement(queryGetTimetable);
			stmntAddTimetable = conn.prepareStatement(queryAddTimetable);
		} catch (SQLException e){
			e.printStackTrace();
			throw new DBException();
		}
	}

	public List<String> getVersions(){
		try{
			ResultSet res = stmntGetVersions.executeQuery();
			List<String> ret = new ArrayList<>();
			while (res.next())
				ret.add(res.getString(1));
			return ret;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public String getTimetable(String version){
		try{
			stmntGetTimetable.setString(1, version);
			ResultSet res = stmntGetTimetable.executeQuery();
			if (!res.next())
				return null;
			return res.getString(1);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean addTimetable(String version, String timetable){
		try{
			stmntAddTimetable.setString(1, version);
			stmntAddTimetable.setString(2, timetable);
			return stmntAddTimetable.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}
