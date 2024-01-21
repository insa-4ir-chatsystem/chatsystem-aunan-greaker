package chatsystem.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**	Singleton class that represents and communicates with the SQLite Database that is stored on the local computer*/
public class Database {
	private static final Logger LOGGER = LogManager.getLogger(ChatHistory.class);
	private static final Database INSTANCE = new Database();
	// Storage location of the Database
	private String url = "jdbc:sqlite:chatsystem_database.db";
	
	private Database() {
	}
	
	public static Database getInstance() {
    	return INSTANCE;
    }
	
	/** Create a new table in the local Database*/
	public synchronized void newTable(String tableName) throws TableAlreadyExists {
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
			catch (SQLException e) {
				LOGGER.error("SQLException when attempting to add message to table in Database in addToTable: " + e);
			}
		}
	}
	
	/** Add a new message to a table in the local Database*/
	public synchronized void addToTable(String tableName, String from, String msg) {
		try (Connection conn = DriverManager.getConnection(url)) {
			Statement stmt = conn.createStatement();
			  
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			String sqlite = "INSERT INTO " + tableName + " VALUES ('" + java.util.UUID.randomUUID().toString() + "', '" + from + "', '" + msg + "', '" + (dtf.format(LocalDateTime.now())).toString() + "');";
			stmt.executeUpdate(sqlite);
			conn.close();
		} catch (SQLException e) {
			LOGGER.error("SQLException when attempting to add message to table in Database in addToTable: " + e);
		}
		
	}
	
	/** Get table from the local Database as a ResultSet*/
	public synchronized ResultSet getTable(String tableName) {  
		try (Connection conn = DriverManager.getConnection(url)) {
			Statement stmt = conn.createStatement();
			String sqlite = "SELECT msgId, fromContact, msg, createdAt FROM " + tableName + ";";
			return stmt.executeQuery(sqlite);
		} catch (SQLException e) {
			LOGGER.error("SQLException when attempting to remove table from Database in removeTable: " + e);
		}
		return null;
	}
	
	/** Remove a table from the local Database*/
	public synchronized void removeTable(String tableName) {
		try (Connection conn = DriverManager.getConnection(url)) {
			Statement stmt = conn.createStatement();
		    
			//Drop the table if it exists in the Database
		    String sqlite = "DROP TABLE IF EXISTS " + tableName + ";";
		    stmt.executeUpdate(sqlite);
		} catch (SQLException e) {
			LOGGER.error("SQLException when attempting to remove table from Database in removeTable: " + e);
		}
	}
	
	/** Returns boolean signifying if table exists in the local Database*/
	public synchronized boolean hasTable(String tableName) {
		try (Connection conn = DriverManager.getConnection(url)) {
		    // Get 'all' tables with the tableName from the parameter
		    ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);
		    // Get the first element of the ResultSet and close the connection
		    rs.next();
		    conn.close();
		        
		    // If the row of the first element in the ResultSet is more than 0, the table exists
		    return rs.getRow() > 0;
		} catch (SQLException e) {
			LOGGER.error("SQLException when attempting to remove table from Database in removeTable: " + e);
		}
		return false;
    }
	
	/** Clear the entire Database, erase all stored tables*/
	public synchronized void clear() {
		try (Connection conn = DriverManager.getConnection(url)) {
			Statement stmt = conn.createStatement();
			// Get all the names of the tables stored in the Database
		    ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
		    
		    // Drop all tables except for the "sqlite_schema" from the Database
		    while (rs.next()) {
		    	if (!rs.getString("TABLE_NAME").equals("sqlite_schema")) {
		    		String sqlite = "DROP TABLE " + rs.getString("TABLE_NAME") + ";";
			        stmt.executeUpdate(sqlite);
		    	}
		    }
		    // Close the connection
		    conn.close();
		} catch (SQLException e) {
			LOGGER.error("SQLException when attempting to clear the Database: " + e);
		}
	}
}
