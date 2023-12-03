package slowport.db;

import java.sql.*;
import java.io.*;

public class Connector{
	private static String FILENAME = System.getProperty("user.home") +
		"/.local/share/slowport/db.sqlite";
	private static String DIR = System.getProperty("user.home") +
		"/.local/share/slowport";
	public static Connection connect(){
		File dir = new File(DIR);
		if (!dir.exists()){
			dir.mkdirs();
		}
		Connection conn = null;
		try{
			conn = DriverManager.getConnection("jdbc:sqlite:" + FILENAME);
		} catch (SQLException e){
			e.printStackTrace();
			conn = null;
		}
		return conn;
	}
}
