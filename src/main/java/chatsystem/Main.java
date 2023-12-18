package chatsystem;

import chatsystem.contacts.*;
import chatsystem.controller.Controller;
import chatsystem.network.UDPListener;
import chatsystem.network.UDPSender;
import chatsystem.ui.View;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

	public static final int BROADCAST_PORT = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final int BROADCAST_REPLY_PORT = 7472; // The port to reply to when receiving a broadcast.
	public static final String ANNOUNCE_PROTOCOL = "All online users announce yourselves.";
	public static String myUsername;
	
    public static void main(String[] args) throws InterruptedException, UnknownHostException {
    	LOGGER.trace("LocalHost " + InetAddress.getLocalHost().getHostAddress());
        Configurator.setRootLevel(Level.INFO);
        LOGGER.info("Starting ChatSystem application");

        View.initialize();
        
        try {
            UDPListener server = new UDPListener(BROADCAST_PORT);

            server.addObserver(msg -> {Controller.handleContactDiscoveryMessage(msg);});

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
			} catch (IOException e) {
				System.err.println("Could not start send broadcast: " + e.getMessage());
	            System.exit(1);
			}
		    
		    Thread.sleep(2000); // Waits 2 seconds for online users to announce themselves
		    
		    Contact newContact = new Contact(myUsername, InetAddress.getLoopbackAddress());
		    if (!ContactList.getInstance().hasContact(newContact)) {
		    	validUsername = true;
		    }
		    else {
		    	System.out.println("The username you chose is already taken! Please choose another username");
		    }
		}
		
		LOGGER.info("Now online with username:" + myUsername);
		
    }
}