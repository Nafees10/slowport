package slowport.db;

import java.sql.*;
import java.util.*;
import slowport.common.*;

public class NoteDB{
	private static final String queryCreateTable =
"""
CREATE TABLE IF NOT EXISTS notes(
		course VARCHAR(255) NOT NULL,
		section VARCHAR(255) NOT NULL,
		note TEXT,
		PRIMARY KEY (course, section));
""";

	private static final String queryGetNotes =
"""
SELECT course, section, note FROM notes;
""";

	private static final String queryGetNotesByCourse =
"""
SELECT course, section, note FROM notes WHERE course=? AND section=?;
""";

	private static final String queryAddNote =
"""
INSERT INTO notes (course, section, note) VALUES (?, ?, ?);
""";

	private static final String queryRemoveNote =
"""
DELETE FROM notes WHERE course=? AND section=?;
""";

	private PreparedStatement stmntGetNotes, stmntGetNotesByCourse, stmntAddNote,
					stmntRemoveNote;

	private Note create(ResultSet res){
		try{
			if (!res.next())
				return null;
			Note note = new Note(
					res.getString(1), res.getString(2), res.getString(3));
			return note;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public NoteDB(Connection conn) throws DBException{
		try{
			Statement stmnt = conn.createStatement();
			stmnt.executeUpdate(queryCreateTable);
		} catch (SQLException e){
			e.printStackTrace();
		}

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

	public Note getNote(String course, String section){
		try{
			stmntGetNotesByCourse.setString(1, course);
			stmntGetNotesByCourse.setString(2, section);
			ResultSet res = stmntGetNotesByCourse.executeQuery();
			return create(res);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean addNote(Note note){
		try{
			stmntAddNote.setString(1, note.getCourse());
			stmntAddNote.setString(2, note.getSection());
			stmntAddNote.setString(3, note.getNote());
			return stmntAddNote.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeNote(String course, String section){
		try{
			stmntRemoveNote.setString(1, course);
			stmntRemoveNote.setString(2, section);
			return stmntRemoveNote.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}
