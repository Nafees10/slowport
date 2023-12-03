package slowport.db;

import java.sql.*;
import java.util.*;
import slowport.common.*;

public class NoteDB{
	private static final String queryCreateTable =
"""
CREATE TABLE IF NOT EXISTS selections(
		week VARCHAR(255) NOT NULL,
		course VARCHAR(255) NOT NULL,
		section VARCHAR(255) NOT NULL,
		sessionIndex INTEGER,
		note TEXT,
		PRIMARY KEY (week, course, section, sessionIndex));
""";

	private static final String queryGetNotes =
"""
SELECT week, course, section, sessionIndex, note FROM notes;
""";

	private static final String queryGetNotesByCourse =
"""
SELECT week, course, section, sessionIndex, note FROM notes
WHERE course=? AND section=?;
""";

	private static final String queryAddNote =
"""
INSERT INTO notes (week, course, section, sessionIndex, note) VALUES
(?, ?, ?, ?, ?);
""";

	private static final String queryRemoveNote =
"""
DELETE FROM notes WHERE week=? AND course=? AND section=? AND sessionIndex=?;
""";

	private PreparedStatement stmntGetNotes, stmntGetNotesByCourse, stmntAddNote,
					stmntRemoveNote;

	private Note create(ResultSet res){
		try{
			if (!res.next())
				return null;
			Note note = new Note(
					new YearWeek(res.getString(1)),
					res.getString(2),
					res.getString(3),
					res.getInt(4),
					res.getString(5)
					);
			return note;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
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

		try{
			Statement stmnt = conn.createStatement();
			stmnt.executeUpdate(queryCreateTable);
		} catch (SQLException e){
			e.printStackTrace();
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

	public List<Note> getNote(String course, String section){
		try{
			stmntGetNotesByCourse.setString(1, course);
			stmntGetNotesByCourse.setString(2, section);
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
			stmntAddNote.setString(1, note.getWeek().toString());
			stmntAddNote.setString(2, note.getCourse());
			stmntAddNote.setString(3, note.getSection());
			stmntAddNote.setInt(4, note.getSessionIndex());
			stmntAddNote.setString(5, note.getNote());
			return stmntAddNote.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeNote(Note note){
		try{
			stmntRemoveNote.setString(1, note.getWeek().toString());
			stmntRemoveNote.setString(2, note.getCourse());
			stmntRemoveNote.setString(3, note.getSection());
			stmntRemoveNote.setInt(4, note.getSessionIndex());
			return stmntRemoveNote.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}
