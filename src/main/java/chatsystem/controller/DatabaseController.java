package chatsystem.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.log.Database;
import chatsystem.contacts.ContactList;
import chatsystem.log.ChatHistory;
import chatsystem.ui.ChatSystemGUI;


public class DatabaseController {
	
	private static final Logger LOGGER = LogManager.getLogger(DatabaseController.class);

	public static void sendMsgHandler(Contact chattingWith, String msg) {
		try {
			ChatHistory chatHistory = new ChatHistory(chattingWith.ip());
			chatHistory.addMessage(InetAddress.getLocalHost(), msg);
		} catch (UnknownHostException e) {
			LOGGER.error("Could not get local host in DatabaseController: " + e);
		}
	}
}
