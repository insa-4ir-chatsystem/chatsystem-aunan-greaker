package chatsystem.contacts;

import java.util.ArrayList;
import java.util.List;

/** List of connected users. */
public class ContactList {
	
    public interface Observer {
        void newContactAdded(Contact contact);
        void nicknameChanged(Contact newContact, String previousNickname);
    }
    
	private static final ContactList INSTANCE = new ContactList();
    List<Contact> contacts = new ArrayList<>();
    List<Observer> observers = new ArrayList<>();
    
    /* Defines singleton constructor class */
    public static ContactList getInstance() {
    	return INSTANCE;
    }
    
    private ContactList() {
    }
    
    public synchronized void addObserver(Observer obs) {
        this.observers.add(obs);
    }

    public synchronized void addContact(Contact contact) throws ContactAlreadyExists {
        if (hasContact(contact)) {
            throw new ContactAlreadyExists(contact);
        } 
        else {
            contacts.add(contact);
            for (Observer obs : observers) {
                obs.newContactAdded(contact);
            }
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
    
    public synchronized void clear() {
        this.contacts.clear();
    }
}