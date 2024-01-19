package chatsystem.controller;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactAlreadyExists;
import chatsystem.contacts.ContactList;
import chatsystem.network.udp.UDPListener;
import chatsystem.network.udp.UDPMessage;
import chatsystem.network.udp.UDPSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPController {

    private static final Logger LOGGER = LogManager.getLogger(UDPController.class);
	public static final int BROADCAST_PORT = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final String ANNOUNCE_REQUEST_MSG = "All online users announce yourselves.";
	public static final String LOGOUT_MSG = "I am logging out.";
	public static final String ANNOUNCE_CHANGED_USERNAME_PREFIX = "I have changed my username to: ";
	private static UDPListener udpListener;

    public static void contactDiscoveryMessageHandler(UDPMessage message) {
		switch (message.text()) {
			case ANNOUNCE_REQUEST_MSG:
				try {
					/** Broadcast our username on the network */
					UDPSender.send(message.source(), BROADCAST_PORT, Controller.getMyUsername());
					LOGGER.debug("Announced ourself to: " + message.source() + ":" + BROADCAST_PORT);
				} catch (IOException e) {
					LOGGER.error("Could not announce ourselves: " + e.getMessage());
				} catch (NullPointerException e) {
					LOGGER.error("Could not announce ourselves because myUsername is null: " + e.getMessage());
				}
				break;
			case LOGOUT_MSG:
				/**	Removes contact from contact list */
				Contact contactToRemove = ContactList.getInstance().getContact(message.source());
				ContactList.getInstance().removeContact(contactToRemove);

				/**	Disables sendbutton if contact logges off */
				if (Controller.getGui() != null
					&& Controller.getGui().getshowingChatWith() != null
					&& Controller.getGui().getshowingChatWith().equals(contactToRemove)) {

					Controller.getGui().disableSendButton();
				}

				LOGGER.info(contactToRemove + " is now offline.");
				break;
			/**	Somebody connecting to the chat */
			default:
				/**	Checks if this is a change of username case */
				if (message.text().startsWith(ANNOUNCE_CHANGED_USERNAME_PREFIX)) {
					/**	Updates contact username */
					String newUsername = message.text().substring(ANNOUNCE_CHANGED_USERNAME_PREFIX.length());
					Contact oldContact = ContactList.getInstance().getContact(message.source());
					Contact newContact = new Contact(newUsername, oldContact.ip());
					ContactList.getInstance().replaceContact(oldContact, newContact);
				}
				/**	Case of new user logging in */
				else {
				Contact newContact = new Contact(message.text(), message.source());
				try {
					ContactList.getInstance().addContact(newContact);
					LOGGER.info(newContact + " is online.");
					LOGGER.debug("ContactList: " + ContactList.getInstance().toString());
				} catch (ContactAlreadyExists e) {
					LOGGER.error("Received duplicated contact: " + newContact);
				}
				break;
				}
		}
    }
    
    public static void announceUsernameChange(String username) {
    	try {
			// Sends its new username on the network so others can replace the old username with the new one
			UDPSender.sendBroadcast(UDPController.BROADCAST_PORT, ANNOUNCE_CHANGED_USERNAME_PREFIX + username);
			LOGGER.trace("Sent UDP broadcast with new username: " + username + " on port: " + UDPController.BROADCAST_PORT);
		} catch (IOException e) {
			LOGGER.error("Failed to send UDP broadcast: " + e.getMessage());
		}
    }
    
    public static Boolean usernameAvailableHandler(String username) {
		LOGGER.debug("Checking if username '" + username + "' is available...");
		if (Controller.isOnline()) {
			LOGGER.trace("We are online, checking if username is in contact list...");
			return ContactList.getInstance().getAllUsernames().contains(username);
		}

		// We are not online
    	try {
            UDPListener server = new UDPListener(BROADCAST_PORT);
            server.addObserver(msg -> {UDPController.contactDiscoveryMessageHandler(msg);});
            server.start();
            
            Controller.setMyUsername(username);  // Gets the chosen username
			
    		try {
    			UDPSender.sendBroadcast(BROADCAST_PORT, ANNOUNCE_REQUEST_MSG); // Sends ANNOUNCE msg to request online users to announce themselves.
    			Thread.sleep(2000); // Waits 2 seconds for all online users to announce themselves
    		} catch (IOException e) {
    			LOGGER.error("Could not start send broadcast: " + e.getMessage());
    		} catch (InterruptedException e) {
    			LOGGER.error("Could not sleep thread ContactList may not have been initialised correctly: " + e.getMessage());
    		}    
    		    
			/** Creates itself has a contact to check if the username is already taken */
    		Contact newContact = new Contact(Controller.getMyUsername(), InetAddress.getLoopbackAddress());
    		if (!ContactList.getInstance().hasContact(newContact)) {
    			// Username chosen is available
    			server.close();
    		    return true;
    		}
    		else {
    			// Username chosen is not available / already taken
    			server.close();
    		    return false;
    		}  		
        } catch (SocketException e) {
            System.err.println("Could not start UDP listener: " + e.getMessage());
            System.exit(1);
        }	
    	return false;
    }

    public static void initilizeUDPListener() {
		try {
			udpListener = new UDPListener(BROADCAST_PORT);
			udpListener.addObserver(msg -> {UDPController.contactDiscoveryMessageHandler(msg);});
			udpListener.start();
		} catch (SocketException e) {
			LOGGER.fatal("Could not start UDP listener: " + e.getMessage());
		}	
	}

	public static void closeUDPListener() {
		try {
			udpListener.close();
		} catch (NullPointerException e) {
			LOGGER.warn("Tried to close UDPListener but it was not running");
		}
	}
}