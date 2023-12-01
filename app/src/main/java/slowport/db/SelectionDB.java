package slowport.db;

import java.util.*;
import java.sql.*;
import slowport.common.*;

public class SelectionDB{
	private static final String queryGetSelections =
"""
SELECT course, section FROM selections;
""";

	private static final String queryRemoveSelection =
"""
DELETE FROM selections WHERE course=?;
""";

	private static final String queryAddSelection =
"""
INSERT INTO selections (course, section) VALUES (?, ?);
""";

	private PreparedStatement stmntGetSelections, stmntRemoveSelection,
					stmntAddSelection;

}
