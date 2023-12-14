package chatsystem.controller;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactAlreadyExists;
import chatsystem.contacts.ContactList;
import chatsystem.network.UDPMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {

    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    public static void handleContactDiscoveryMessage(UDPMessage message) {
    	Contact contact = new Contact(message.text(), message.source());
        String username = message.text();
        try {
            ContactList.getInstance().addContact(contact);
            LOGGER.info("New Contact added to the list: " + contact);
        } catch (ContactAlreadyExists e) {
            LOGGER.error("Received duplicated contact: " + contact);
        }
    }
}