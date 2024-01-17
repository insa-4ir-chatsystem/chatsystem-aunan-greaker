package chatsystem.controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.event.ListSelectionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.log.Database;
import chatsystem.ui.ChatSystemGUI;


public class DatabaseController {
	
	private static final Logger LOGGER = LogManager.getLogger(DatabaseController.class);

	public static void sendMsgHandler(Contact chattingWith, String msg) {
		Database db = Database.getInstance();
		try {
			db.addToTable(chattingWith.ip().toString(), "me", msg);
		} catch (SQLException e) {
			LOGGER.error("AddToTable function of Database failed with error: " + e);
		}
	}
}
