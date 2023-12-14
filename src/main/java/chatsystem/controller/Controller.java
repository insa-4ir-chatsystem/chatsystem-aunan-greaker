package chatsystem.controller;

import chatsystem.Main;
import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactAlreadyExists;
import chatsystem.contacts.ContactList;
import chatsystem.network.UDPMessage;
import chatsystem.network.UDPSender;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {

    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    public static void handleContactDiscoveryMessage(UDPMessage message) {
    	if (!message.text().equals(Main.ANNOUNCE_PROTOCOL))
    	{
	    	Contact contact = new Contact(message.text(), message.source());
	        try {
	            ContactList.getInstance().addContact(contact);
	            LOGGER.info("New Contact added to the list: " + contact);
	        } catch (ContactAlreadyExists e) {
	            LOGGER.error("Received duplicated contact: " + contact);
	        }
    	}
    	else {
    		try {
				UDPSender.send(message.source(), Main.BROADCAST_PORT, Main.myUsername);
				LOGGER.info("Announced ourself to: " + message.source());
			} catch (IOException e) {
				LOGGER.error("Could not announce ourselves: " + e.getMessage());
			}
    	}
    }
  
}