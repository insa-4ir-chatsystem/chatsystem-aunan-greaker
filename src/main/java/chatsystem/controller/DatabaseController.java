package chatsystem.controller;

import java.sql.SQLException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.log.Database;


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
