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
        Controller.setMyUsername("Eve");
        UDPMessage msg1 = new UDPMessage(UDPController.ANNOUNCE_REQUEST_MSG, InetAddress.getByName("10.5.5.1"));
        UDPMessage msg2 = new UDPMessage(UDPController.ANNOUNCE_REQUEST_MSG, InetAddress.getByName("10.5.5.2"));

        UDPController.UDPMessageHandler(msg1);
        UDPController.UDPMessageHandler(msg2);

        /** Testing Username msg*/
        ContactList contacts = ContactList.getInstance();
        UDPMessage msg3 = new UDPMessage("alice", InetAddress.getByName("10.5.5.1"));
        UDPMessage msg4 = new UDPMessage("bob", InetAddress.getByName("10.5.5.2"));
        
        Contact contact1 = new Contact(msg3.text(), msg3.source());
        Contact contact2 = new Contact(msg4.text(), msg4.source());
        
        assert !contacts.hasContact(contact1);
        UDPController.UDPMessageHandler(msg3);
        assert contacts.hasContact(contact1);
        assert !contacts.hasContact(contact2);

        UDPController.UDPMessageHandler(msg4);
        assert contacts.hasContact(contact2);

        UDPController.UDPMessageHandler(msg4);

        /** Testing LOGOUT_MSG */
        UDPMessage msg5 = new UDPMessage(UDPController.LOGOUT_MSG, InetAddress.getByName("10.5.5.1"));
        UDPMessage msg6 = new UDPMessage(UDPController.LOGOUT_MSG, InetAddress.getByName("10.5.5.2"));

        UDPController.UDPMessageHandler(msg5);
        UDPController.UDPMessageHandler(msg6);
    }
}