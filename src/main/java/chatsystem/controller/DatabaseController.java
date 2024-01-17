package chatsystem.controller;

import java.sql.SQLException;

import javax.swing.event.ListSelectionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.log.Database;


public class DatabaseController {
	
	private static final Logger LOGGER = LogManager.getLogger(DatabaseController.class);

	public static void sendMsgHandler(String msg) {
		
	}
	
	public static void selectedRowHandler(String contactUsername ) {
		// Get the 'Chat' table to the selected username
        ContactList contList = ContactList.getInstance(); 
        Contact selectedContact = contList.getContact(contactUsername);
        Database db = Database.getInstance();
        try {
			db.getTable(selectedContact.ip().toString());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
