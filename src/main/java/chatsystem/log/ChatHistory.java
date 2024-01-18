package chatsystem.log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.controller.DatabaseController;

public class ChatHistory {
	private InetAddress self;
	private InetAddress other;
	private List<ChatMessage> chatHistory = new ArrayList<ChatMessage>();
	
	private static final Logger LOGGER = LogManager.getLogger(ChatHistory.class);
	
	public ChatHistory(InetAddress other) {
		try {
			self = InetAddress.getLocalHost();
			this.other = other;
		} catch (UnknownHostException e) {
			LOGGER.error("Could not get the local host address: " + e);
			e.printStackTrace();
		}
		
		try {
			Database db = Database.getInstance();
			if (!db.hasTable(other.toString())) {
				db.newTable(other.toString());
			}
	        ResultSet rs = db.getTable(other.toString());
	        
	        // Populate the chatHistory with data from the Database
	        while (rs.next()) {
	        	String msg_id = rs.getString("msg_id");
	            InetAddress from = InetAddress.getByName(rs.getString("from_contact"));
	            String msg = rs.getString("msg");
	            String timeStamp = rs.getString("created_at");
	
	            // Add a new chatMessage to the chatHistory
	            chatHistory.add(new ChatMessage(msg_id, from, msg, timeStamp));
	        }
	        
        } catch (SQLException e) {
        	LOGGER.error("Could not get the table from the Database: " + e.getMessage());
        } catch (UnknownHostException e) {
			LOGGER.error("Could not change 'from_contact' value of database to an InetAddress: " + e);
			e.printStackTrace();
		} catch (TableAlreadyExists e) {
			LOGGER.error("Table already exist error in ChatHistory construction: " + e.getMessage());
		}
	}
	
	public void addMessage(String msg) {
		// Add message to the table in the Database
        try {
        	Database db = Database.getInstance();
			db.addToTable(other.toString(), self.toString(), msg);
		} catch (SQLException e) {
			LOGGER.error("Could not add msg to database: " + e.getMessage());
		}
        
        // Add message to the chatHistory list of this instance of the class
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        chatHistory.add(new ChatMessage("new", other, msg, (dtf.format(LocalDateTime.now())).toString()));
	}
	
	public List<ChatMessage> getChatHistory() {
		return chatHistory;
	}
}
