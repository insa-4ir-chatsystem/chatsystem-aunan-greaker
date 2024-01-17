package chatsystem.controller;

import chatsystem.contacts.ContactList;
import chatsystem.contacts.Contact;
import chatsystem.network.udp.UDPMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ControllerTest {
    private static final int SLEEP_DELAY = 100;
    @BeforeEach
    void clearContactList() {
        ContactList.getInstance().clear();
    }

    @BeforeEach
    void closeListener() throws InterruptedException {
        UDPController.closeUDPListener();
        Thread.sleep(SLEEP_DELAY);
    }

    @Test
    void messageHandlingTest() throws UnknownHostException {
        /** Testing ANNOUNCE_REQUEST_MSG */
        UDPController.myUsername = "Eve";
        UDPMessage msg1 = new UDPMessage(UDPController.ANNOUNCE_REQUEST_MSG, InetAddress.getByName("10.5.5.1"));
        UDPMessage msg2 = new UDPMessage(UDPController.ANNOUNCE_REQUEST_MSG, InetAddress.getByName("10.5.5.2"));

        UDPController.contactDiscoveryMessageHandler(msg1);
        UDPController.contactDiscoveryMessageHandler(msg2);

        /** Testing Username msg*/
        ContactList contacts = ContactList.getInstance();
        UDPMessage msg3 = new UDPMessage("alice", InetAddress.getByName("10.5.5.1"));
        UDPMessage msg4 = new UDPMessage("bob", InetAddress.getByName("10.5.5.2"));
        
        Contact contact1 = new Contact(msg3.text(), msg3.source());
        Contact contact2 = new Contact(msg4.text(), msg4.source());
        
        assert !contacts.hasContact(contact1);
        try {
            UDPController.contactDiscoveryMessageHandler(msg3);
        } catch (NullPointerException e) {
            // GUI is not initialized
        }
        
        assert contacts.hasContact(contact1);

        assert !contacts.hasContact(contact2);

        try {
            UDPController.contactDiscoveryMessageHandler(msg4);
        } catch (NullPointerException e) {
            // GUI is not initialized
        }
        assert contacts.hasContact(contact2);

        try {
            UDPController.contactDiscoveryMessageHandler(msg4);
        } catch (NullPointerException e) {
            // GUI is not initialized
        }

        /** Testing LOGOUT_MSG */
        UDPMessage msg5 = new UDPMessage(UDPController.LOGOUT_MSG, InetAddress.getByName("10.5.5.1"));
        UDPMessage msg6 = new UDPMessage(UDPController.LOGOUT_MSG, InetAddress.getByName("10.5.5.2"));

        try {
            UDPController.contactDiscoveryMessageHandler(msg5);
            UDPController.contactDiscoveryMessageHandler(msg6);
        } catch (NullPointerException e) {
            // GUI is not initialized
        }
    }

    @Test
    void loginLogoutHandlersTest() throws UnknownHostException, InterruptedException {
        UDPController.myUsername = "Eve";
        UDPController.loginHandler();
        UDPController.logoutHandler();
    }
}