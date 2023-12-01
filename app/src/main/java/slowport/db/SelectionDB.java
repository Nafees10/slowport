package slowport.db;

import java.util.*;
import java.sql.*;
import slowport.common.*;
import slowport.filter.*;
import slowport.db.*;

public class SelectionDB{
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

	public boolean addSelected(Session session){
		try{
			stmntAddSelection.setString(1, session.getName());
			stmntAddSelection.setString(2, session.getSection());
			return stmntAddSelection.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeSelected(Session session){
		try{
			stmntRemoveSelection.setString(1, session.getName());
			stmntRemoveSelection.setString(2, session.getSection());
			return stmntRemoveSelection.executeUpdate() >= 1;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
}
