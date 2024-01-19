package chatsystem.contacts;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** List of connected users. */
public class ContactList {
	
    public interface Observer {
        void newContactAdded(Contact contact);
        void contactRemoved(Contact contact);
        void usernameChanged(String newUsername, String oldUsername);
    }
    
    private static final Logger LOGGER = LogManager.getLogger(ContactList.class);
	private static final ContactList INSTANCE = new ContactList();
    List<Contact> contacts = new ArrayList<>();
    List<Observer> observers = new ArrayList<>();
    
    private ContactList() {
    }

    /* Defines singleton constructor class */
    public static ContactList getInstance() {
    	return INSTANCE;
    }

    public synchronized void addObserver(Observer obs) {
        this.observers.add(obs);
    }

    public synchronized void removeObserver(Observer obs) {
        this.observers.remove(obs);
    }

    public synchronized Contact getContact(String username) {
        for (Contact contact : contacts) {
            if (contact.username().equals(username)) {
                return contact;
            }
        }
        return null;
    }

    public synchronized Contact getContact(InetAddress ip) {
        for (Contact contact : contacts) {
            if (contact.ip().equals(ip)) {
                return contact;
            }
        }
        return null;
    }

    public synchronized void addContact(Contact contact) throws ContactAlreadyExists {
        if (hasContact(contact)) {
            throw new ContactAlreadyExists(contact);
        } else {
            contacts.add(contact);
            for (Observer obs : observers) {
                obs.newContactAdded(contact);
            }
        }
    }

    public synchronized void removeContact(Contact contact) {
        if (hasContact(contact)) {
            contacts.remove(contact);
            LOGGER.trace("Contact removed from the list: " + contact);
            for (Observer obs : observers) {
                obs.contactRemoved(contact);
            }
        }
        else {
            LOGGER.warn("Tried to remove a contact that is not in the list: " + contact);
        }
    }
    
    public synchronized void replaceContact(Contact oldContact, Contact newContact) {
        if (hasContact(oldContact)) {
        	for (int i = 0; i < contacts.size(); i++) {
        		if (contacts.get(i).equals(oldContact)) {
        			contacts.set(i, newContact);
                    for (Observer obs : observers) {
                        obs.usernameChanged(newContact.username(), oldContact.username());
                    }
        		}
        	}
            LOGGER.trace("Contact replaced in the list: " + oldContact + " -> " + newContact);
        }
        else {
            LOGGER.warn("Tried to replace a contact that is not in the list: " + oldContact);
        }
    }

    public synchronized boolean hasContact(Contact contact) {
    	return contacts.contains(contact);
    }
    
    public synchronized boolean hasContactIP(Contact contact) {
    	List<Contact> allContacts = getAllContacts();
    	for (int i = 0; allContacts.size() > i; i++) {
    		if ((allContacts.get(i).ip()).equals(contact.ip())) {
    			return true;
    		}
    	}
    	return false;
    }

    public synchronized List<Contact> getAllContacts() {
        // return defensive copy of the contacts to avoid anybody modifying it or doing unsynchronized access
        return new ArrayList<>(this.contacts);
    }

    public synchronized List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (Contact contact : contacts) {
            usernames.add(contact.username());
        }
        return usernames;
    }

    public synchronized String toString() {
        return this.contacts.toString();
    }
    
    public synchronized void clear() {
        this.contacts.clear();
    }
}