package chatsystem.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.network.tcp.TCPConnection;
import chatsystem.network.tcp.TCPListener;

public class TCPController {
	private static final Logger LOGGER = LogManager.getLogger(TCPController.class);
	private static TCPListener theTCPListener;
	public static final int TCP_LISTENING_PORT = 9922;
	
	/**	Starts the TCPListener. This is required to be able to accept incoming TCPConnections*/
	public static void startTCPListener() {
		try {
			theTCPListener = new TCPListener(TCP_LISTENING_PORT);
			theTCPListener.addObserver(socket -> TCPController.handleIncomingTCPConnection(socket));
			theTCPListener.start();
			LOGGER.trace("TCPListener started on port " + TCP_LISTENING_PORT);
		} catch (IOException e) {
			LOGGER.error("Could not start TCPListener");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void stopTCPListener() {
		try {
			theTCPListener.close();
		} catch (IOException e) {
			LOGGER.error("Could not stop TCPListener");
			e.printStackTrace();
		} catch (NullPointerException e) {
			LOGGER.info("Tried to close TCPListener but it was not running");
		}
	}
	
	/**	Method to be called by TCPListener when a new remote user wants to start chat with us*/
	public static void handleIncomingTCPConnection(Socket socket) {
		// Handles Incoming TCPConnection in a separate thread so TCPListener can continue listen for new connections
		Thread handlerThread = new Thread(() -> {
			try {
				TCPConnection chatConnection = new TCPConnection(socket);
				LOGGER.trace("Incoming TCPConnection established with " + socket.getInetAddress() + " on port " + socket.getPort());

				//Handles incoming messages
				String receivedMsg;
				while ((receivedMsg = chatConnection.readMessage()) != null) {
					LOGGER.trace("Received message: " + receivedMsg);
					
					// Update the chat history table in GUI
					ContactList contactList = ContactList.getInstance();
					Contact otherUser = contactList.getContact(chatConnection.getIp());
					Controller.getGui().updateChatsTable(otherUser);
					
				}
				LOGGER.trace("TCPConnection with " + socket.getInetAddress() + " on port " + socket.getPort() + " closed");
			} catch (IOException e) {
				System.err.println("Could not establish TCPConnection with " + socket.getInetAddress());
				e.printStackTrace();
			}

		});
		handlerThread.start();
	}
	
	/** Starts a chat with remote user on given ip*/
	public static TCPConnection startChatWith(InetAddress ip){
		try {
			return new TCPConnection(ip, TCP_LISTENING_PORT);
		} catch (IOException e) {
			LOGGER.error("Could not start TCPConnection with " + ip + "on port " + TCP_LISTENING_PORT);
			e.printStackTrace();
			return null;
		}
	}
}
