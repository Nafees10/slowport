package slowport.db;

import java.sql.*;
import java.util.*;
import slowport.common.*;

public class NoteDB{
	private static String queryGetNotes =
"""
SELECT week, course, sessionIndex, note FROM notes;
""";

	private static String queryGetNotesByCourse =
"""
SELECT week, course, sessionIndex, note FROM notes WHERE course=?;
""";

	private static String queryAddNote =
"""
INSERT INTO notes (week, course, sessionIndex, note) VALUES (?, ?, ?, ?);
""";

	private PreparedStatement stmntGetNotes, stmntGetNotesByCourse, stmntAddNote;

	private Note create(ResultSet res){
		try{
			if (!res.next())
				return null;
			Note note = new Note(
					res.getInt(1),
					res.getString(2),
					res.getInt(3),
					res.getString(4)
					);
			return note;
		} catch (SQLException e){
			return null;
		}
	}

	public NoteDB(Connection conn) throws DBException{
		try{
			stmntGetNotes = conn.prepareStatement(queryGetNotes);
			stmntGetNotesByCourse = conn.prepareStatement(queryGetNotesByCourse);
			stmntAddNote = conn.prepareStatement(queryAddNote);
		} catch (SQLException e ){
			e.printStackTrace();
			throw new DBException();
		}
	}

	public List<Note> getNotes(){
		List<Note> ret = null;
		try{
			ResultSet res = stmntGetNotes.executeQuery();
			Note note = create(res);
			ret; /// TODO continue from here
		}
		return ret;
	}
}
