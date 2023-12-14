package chatsystem.contacts;

import java.util.ArrayList;
import java.util.List;

/** List of connected users. */
public class ContactList {
	public static final int BROADCAST_PORT = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final int BROADCAST_REPLY_PORT = 7472; // The port to reply to when receiving a broadcast.
	private static final ContactList INSTANCE = new ContactList();
    List<Contact> contacts = new ArrayList<>();

    /* Defines singleton constructor class */
    public static ContactList getInstance() {
    	return INSTANCE;
    }
    
    private ContactList() {}

    public synchronized void addContact(Contact contact) throws ContactAlreadyExists {
        if (hasContact(contact)) {
            throw new ContactAlreadyExists(contact);
        } else {
            contacts.add(contact);
        }
    }

    public synchronized boolean hasContact(Contact contact) {
    	// returns true if contact contains another user with the same username. See override of equals in 'chatsystem.contacts.Contact.java'
    	return contacts.contains(contact);
    }

    public synchronized List<Contact> getAllContacts() {
        // return defensive copy of the contacts to avoid anybody modifying it or doing unsynchronized access
        return new ArrayList<>(this.contacts);
    }
    
    public synchronized void clear() {
        this.contacts.clear();
    }
}