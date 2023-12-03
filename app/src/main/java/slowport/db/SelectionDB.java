package slowport.db;

import java.util.*;
import java.sql.*;
import slowport.common.*;
import slowport.filter.*;

public class SelectionDB{
	private static final String queryCreateTable =
"""
CREATE TABLE IF NOT EXISTS selections(
		course VARCHAR(255) NOT NULL,
		section VARCHAR(255) NOT NULL,
		PRIMARY KEY (course, section));
""";

	private static final String queryGetSelections =
"""
SELECT course, section FROM selections;
""";

	private static final String queryRemoveSelection =
"""
DELETE FROM selections WHERE course=? AND section=?;
""";

	private static final String queryAddSelection =
"""
INSERT INTO selections (course, section) VALUES (?, ?);
""";

	private PreparedStatement stmntGetSelections, stmntRemoveSelection,
					stmntAddSelection;
	private TimetableDB timetableStore;

	private List<Session> create(ResultSet res, String version){
		Criterion courseC = null,
							sectionC = null,
							ander = null;
		try{
			if (!res.next())
				return null;
			courseC = new CourseCriterion(res.getString(1));
			sectionC = new SectionCriterion(res.getString(2));
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}
		ander = new AndCriterion(Arrays.asList(courseC, sectionC));
		return (new Filter(ander)).filter(timetableStore.getSessions(version));
	}

	public SelectionDB(Connection conn,
			TimetableDB timetableStore) throws DBException{
		this.timetableStore = timetableStore;
		try{
			Statement stmnt = conn.createStatement();
			stmnt.executeUpdate(queryCreateTable);
		} catch (SQLException e){
			e.printStackTrace();
		}

		try{
			stmntGetSelections = conn.prepareStatement(queryGetSelections);
			stmntRemoveSelection = conn.prepareStatement(queryRemoveSelection);
			stmntAddSelection = conn.prepareStatement(queryAddSelection);
		} catch (SQLException e){
			e.printStackTrace();
			throw new DBException();
		}
	}

	public List<Session> getSelected(String version){
		try{
			ResultSet res = stmntGetSelections.executeQuery();
			List<Session> ret = new ArrayList<>();
			while (true){
				List<Session> sub = create(res, version);
				if (sub == null)
					break;
				ret.addAll(sub);
			}
			if (ret.size() == 0)
				return null;
			return ret;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean addSelected(String course, String section){
		try{
			stmntAddSelection.setString(1, course);
			stmntAddSelection.setString(2, section);
			return stmntAddSelection.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeSelected(String course, String section){
		try{
			stmntRemoveSelection.setString(1, course);
			stmntRemoveSelection.setString(2, section);
			return stmntRemoveSelection.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}
