package chatsystem.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.log.ChatHistory;


public class DatabaseController {
	
	private static final Logger LOGGER = LogManager.getLogger(DatabaseController.class);

	/** Method to be called when a new message is received. Adds the message to the chat history of the sender*/
	public static void addMsgHandler(Contact chattingWith, String msg) {
		try {
			ChatHistory chatHistory = new ChatHistory(chattingWith.ip());
			chatHistory.addMessage(InetAddress.getLocalHost(), msg);
			
			// Add the changes to the chat history table in the GUI
			Controller.getGui().showChatsWith(chattingWith);
		} catch (UnknownHostException e) {
			LOGGER.error("Could not get local host in DatabaseController: " + e);
		}
	}
}
