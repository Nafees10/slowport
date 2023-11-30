package slowport.db;

import java.sql.*;
import java.util.*;
import slowport.common.*;

public class NoteDB{
	private static final String queryGetNotes =
"""
SELECT week, course, sessionIndex, note FROM notes;
""";

	private static final String queryGetNotesByCourse =
"""
SELECT week, course, sessionIndex, note FROM notes WHERE course=?;
""";

	private static final String queryAddNote =
"""
INSERT INTO notes (week, course, sessionIndex, note) VALUES (?, ?, ?, ?);
""";

	private static final String queryRemoveNote =
"""
DELETE FROM notes WHERE week=? AND course=? AND sessionIndex=?;
""";

	private PreparedStatement stmntGetNotes, stmntGetNotesByCourse, stmntAddNote,
					stmntRemoveNote;

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
			e.printStackTrace();
			return null;
		}
	}

	public NoteDB(Connection conn) throws DBException{
		try{
			stmntGetNotes = conn.prepareStatement(queryGetNotes);
			stmntGetNotesByCourse = conn.prepareStatement(queryGetNotesByCourse);
			stmntAddNote = conn.prepareStatement(queryAddNote);
			stmntRemoveNote = conn.prepareStatement(queryRemoveNote);
		} catch (SQLException e){
			e.printStackTrace();
			throw new DBException();
		}
	}

	public List<Note> getNotes(){
		try{
			ResultSet res = stmntGetNotes.executeQuery();
			List<Note> ret = new ArrayList<>();
			while (true){
				Note note = create(res);
				if (note == null)
					break;
				ret.add(note);
			}
			return ret;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public List<Note> getNote(String course){
		try{
			stmntGetNotesByCourse.setString(1, course);
			ResultSet res = stmntGetNotesByCourse.executeQuery();
			List<Note> ret = new ArrayList<>();
			while (true){
				Note note = create(res);
				if (note == null)
					break;
				ret.add(note);
			}
			return ret;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean addNote(Note note){
		try{
			stmntAddNote.setInt(1, note.getWeek());
			stmntAddNote.setString(2, note.getCourse());
			stmntAddNote.setInt(3, note.getSessionIndex());
			stmntAddNote.setString(4, note.getNote());
			return stmntAddNote.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeNote(Note note){
		try{
			stmntRemoveNote.setInt(1, note.getWeek());
			stmntRemoveNote.setString(2, note.getCourse());
			stmntRemoveNote.setInt(3, note.getSessionIndex());
			return stmntRemoveNote.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}
