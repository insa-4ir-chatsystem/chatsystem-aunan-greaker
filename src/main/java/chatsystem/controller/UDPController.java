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
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPController {

    private static final Logger LOGGER = LogManager.getLogger(UDPController.class);
	public static final int BROADCAST_PORT = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final int BROADCAST_REPLY_PORT = 7472; // The port to reply to when receiving a broadcast.
	public static final String ANNOUNCE_PROTOCOL = "All online users announce yourselves.";
	public static String myUsername;

    public static void contactDiscoveryMessageHandler(UDPMessage message) {
    	if (!message.text().equals(ANNOUNCE_PROTOCOL))
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
				UDPSender.send(message.source(), BROADCAST_PORT, myUsername);
				LOGGER.info("Announced ourself to: " + message.source());
			} catch (IOException e) {
				LOGGER.error("Could not announce ourselves: " + e.getMessage());
			}
    	}
    }
    
    public static void loginHandler() {
        try {
            UDPListener server = new UDPListener(BROADCAST_PORT);
            server.addObserver(msg -> {UDPController.contactDiscoveryMessageHandler(msg);});
            server.start();
        } catch (SocketException e) {
            System.err.println("Could not start UDP listener: " + e.getMessage());
            System.exit(1);
        }
        
        Boolean validUsername = false;
		Scanner input = new Scanner(System.in);  // Create a Scanner object
		
		while (!validUsername) {
		    System.out.print("Enter username: ");
		    myUsername = input.nextLine();  // Read user input
		
		    try {
				UDPSender.sendBroadcast(BROADCAST_PORT, ANNOUNCE_PROTOCOL); // Sends ANNOUNCE msg to request online users to announce themselves.
				Thread.sleep(2000); // Waits 2 seconds for online users to announce themselves
		    } catch (IOException e) {
				System.err.println("Could not start send broadcast: " + e.getMessage());
	            System.exit(1);
			} catch (InterruptedException e) {
				System.err.println("Could not sleep thread ContactList may not have been initialised correctly: " + e.getMessage());
			}
		    
		    
		    Contact newContact = new Contact(myUsername, InetAddress.getLoopbackAddress());
		    if (!ContactList.getInstance().hasContact(newContact)) {
		    	validUsername = true;
		    }
		    else {
		    	System.out.println("The username you chose is already taken! Please choose another username");
		    }
		}
		input.close();
		
	    try {
			UDPSender.sendBroadcast(BROADCAST_PORT, myUsername); // Sends its username on the network so others can add it to contactlist
		} catch (IOException e) {
			System.err.println("Could not start send broadcast: " + e.getMessage());
            System.exit(1);
		}
		
		LOGGER.info("Now online with username:" + myUsername);
    }
  
}