package chatsystem.controller;

import chatsystem.contacts.ContactList;
import chatsystem.contacts.Contact;
import chatsystem.controller.Controller;
import chatsystem.network.udp.UDPMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ControllerTest {

    @BeforeEach
    void clearContactList() {
        ContactList.getInstance().clear();
    }

    @Test
    void messageHandlingTest() throws UnknownHostException {
        ContactList contacts = ContactList.getInstance();
        UDPMessage msg1 = new UDPMessage("alice", InetAddress.getByName("10.5.5.1"));
        UDPMessage msg2 = new UDPMessage("bob", InetAddress.getByName("10.5.5.2"));
        
        Contact contact1 = new Contact(msg1.text(), msg1.source());
        Contact contact2 = new Contact(msg2.text(), msg2.source());
        
        assert !contacts.hasContact(contact1);
        Controller.contactDiscoveryMessageHandler(msg1);
        assert contacts.hasContact(contact1);

        assert !contacts.hasContact(contact2);
        Controller.contactDiscoveryMessageHandler(msg2);
        assert contacts.hasContact(contact2);

        Controller.contactDiscoveryMessageHandler(msg2);
    }
}