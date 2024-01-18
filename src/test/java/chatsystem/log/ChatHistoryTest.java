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
		try {
			ChatHistory chatHistory = new ChatHistory(InetAddress.getLocalHost());
			chatHistory.addMessage("Hello");
			
			List<ChatMessage> list = chatHistory.getChatHistory();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			assert list.get((list.size())-1).equals(new ChatMessage("new", InetAddress.getByName("0.0.0.0"), "Hello", (dtf.format(LocalDateTime.now())).toString()));
		} catch (UnknownHostException e) {
			fail("Could not create new ChatHistory instance: " + e);
			e.printStackTrace();
		}
	}

}
