package chatsystem.log;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*	Class that represents the SQLite Database*/
public class Database {

	private static final Database INSTANCE = new Database();
	public String url = "jdbc:sqlite:chatsystem_database.db";
	
	public static Database getInstance() {
    	return INSTANCE;
    }
	
	public synchronized void newTable(String tableName) throws TableAlreadyExists, SQLException {
		if (hasTable(tableName)) {
            throw new TableAlreadyExists(tableName);
        }
		else {
			try (Connection conn = DriverManager.getConnection(url)) {
				Statement stmt = conn.createStatement();
			    
			    String sqlite = "CREATE TABLE " + tableName + " (msgId text PRIMARY KEY, fromContact text NOT NULL, msg text NOT NULL, createdAt text NOT NULL);";
			    stmt.execute(sqlite);
			    conn.close();
			}
		}
	}
	
	public synchronized void addToTable(String tableName, String from, String msg) throws SQLException {
		try (Connection conn = DriverManager.getConnection(url)) {
			Statement stmt = conn.createStatement();
			  
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			String sqlite = "INSERT INTO " + tableName + " VALUES ('" + java.util.UUID.randomUUID().toString() + "', '" + from + "', '" + msg + "', '" + (dtf.format(LocalDateTime.now())).toString() + "');";
			stmt.executeUpdate(sqlite);
			conn.close();
		}
	}
	
	public synchronized ResultSet getTable(String tableName) throws SQLException {  
		Connection conn = DriverManager.getConnection(url);
		Statement stmt = conn.createStatement();
		String sqlite = "SELECT msgId, fromContact, msg, createdAt FROM " + tableName + ";";
		return stmt.executeQuery(sqlite);
	}
	
	public synchronized void removeTable(String tableName) {
		try {
			Connection conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
		    
		    String sqlite = "DROP TABLE IF EXISTS " + tableName + ";";
		    stmt.executeUpdate(sqlite);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public synchronized boolean hasTable(String tableName) throws SQLException{
        Connection conn = DriverManager.getConnection(url);
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, tableName, null);
        rs.next();
        conn.close();
        return rs.getRow() > 0;
    }
	
	public synchronized void clear() throws SQLException {
		try (Connection conn = DriverManager.getConnection(url)) {
			Statement stmt = conn.createStatement();
		    ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
		    
		    while (rs.next()) {
		    	System.out.println(rs.getString("TABLE_NAME"));
		    	if (!rs.getString("TABLE_NAME").equals("sqlite_schema")) {
		    		String sqlite = "DROP TABLE " + rs.getString("TABLE_NAME") + ";";
			        stmt.executeUpdate(sqlite);
		    	}
		    }
		    conn.close();
		}
	}
}
