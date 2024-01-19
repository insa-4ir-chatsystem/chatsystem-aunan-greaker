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


public class ChatHistory {
	private InetAddress other;
	private String tblName;
	private List<ChatMessage> chatHistory = new ArrayList<ChatMessage>();
	
	private static final Logger LOGGER = LogManager.getLogger(ChatHistory.class);
	
	public ChatHistory(InetAddress other) {
		this.other = other;
		tblName = "Chat" + other.getHostAddress();
		tblName = tblName.replaceAll("\\.", "");
		
		try {
			Database db = Database.getInstance();
			if (!db.hasTable(tblName)) {
				db.newTable(tblName);
			}
	        ResultSet rs = db.getTable(tblName);
	        
	        // Populate the chatHistory with data from the Database
	        while (rs.next()) {
	        	String msg_id = rs.getString("msgId");
	            InetAddress from = InetAddress.getByName(rs.getString("fromContact"));
	            String msg = rs.getString("msg");
	            String timeStamp = rs.getString("createdAt");
	
	            // Add a new chatMessage to the chatHistory
	            chatHistory.add(new ChatMessage(msg_id, from, msg, timeStamp));
	        }
	        
        } catch (SQLException e) {
        	LOGGER.error("Could not get the table from the Database: " + e.getMessage());
        } catch (TableAlreadyExists e) {
			LOGGER.error("Table already exist error in ChatHistory construction: " + e.getMessage());
		} catch (UnknownHostException e) {
			LOGGER.error("Could not convert 'fromContact' value to InetAddress: " + e.getMessage());
		}
	}
	
	public void addMessage(InetAddress from, String msg) {
		// Add message to the table in the Database
        try {
        	Database db = Database.getInstance();
			db.addToTable(tblName, from.getHostAddress(), msg);
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
