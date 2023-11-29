package slowport.db;

import java.sql.*;

public class Connector{
	private static String filename = "~/.local/slowport/db.sqlite";
	public static Connection connect(){
		Connection conn = null;
		try{
			conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
		} catch (SQLException e){
			e.printStackTrace();
			conn = null;
		}
		return conn;
	}
}
