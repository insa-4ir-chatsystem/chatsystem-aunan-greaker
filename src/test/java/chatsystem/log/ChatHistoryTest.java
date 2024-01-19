package chatsystem.log;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

class ChatHistoryTest {

	@Test
	void testChatHistory() throws SQLException {
		//Database db = Database.getInstance();
		//db.clear();
		
		try {
			InetAddress test = InetAddress.getLocalHost();
		
			ChatHistory chatHistory = new ChatHistory(test);
			chatHistory.addMessage(test, "Hello");
				
			List<ChatMessage> list = chatHistory.getChatHistory();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			assert list.get((list.size())-1).equals(new ChatMessage("new", test, "Hello", (dtf.format(LocalDateTime.now())).toString()));
		
		} catch (UnknownHostException e) {
			fail("Could not get local host address");
			e.printStackTrace();
		}
	}

}
