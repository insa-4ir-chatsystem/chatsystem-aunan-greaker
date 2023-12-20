package chatsystem.log;

import java.net.InetAddress;
import java.util.List;

/*	Class that represents a ChatHistory between 2 people in the chat system*/
public class ChatHistory {
	private InetAddress self; // The ip of the user himself
	private InetAddress other; // The ip of the user the chat history is with
	private List<ChatMessage> chatMessages; // The list of all chat messages
	
	public ChatHistory(){
		//TODO
	}
	
	public void addMessage(ChatMessage message) {
		//TODO
	}
	
	public List<ChatMessage> getChatHistory(){
		//TODO
		return chatMessages;
	}
}
