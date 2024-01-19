package chatsystem.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.log.ChatHistory;
import chatsystem.network.tcp.TCPConnection;
import chatsystem.network.tcp.TCPListener;

public class TCPController {
	private static final Logger LOGGER = LogManager.getLogger(TCPController.class);
	public static final int TCP_LISTENING_PORT = 9922;
	private static TCPListener theTCPListener;

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
					messageReceivedHandler(receivedMsg, chatConnection.getIp());
				}
				LOGGER.trace("TCPConnection with " + socket.getInetAddress() + " on port " + socket.getPort() + " closed");
			} catch (IOException e) {
				LOGGER.error("Could not establish incoming TCPConnection with " + socket.getInetAddress() + " because " + e.getMessage());
			}

		});
		handlerThread.start();
	}

	public static void messageReceivedHandler(String msg, InetAddress from) {
		LOGGER.trace("Received message: " + msg);
		// Store the incoming message in the local chat history
		InetAddress otherUser = from;
		ChatHistory chatHistory = new ChatHistory(otherUser);
		chatHistory.addMessage(otherUser, msg);

		// Update the GUI if we are currently chatting with the user who sent the message
		Contact contact = ContactList.getInstance().getContact(otherUser); // The contact we are chatting with
		if (Controller.getGui().getshowingChatWith() != null && Controller.getGui().getshowingChatWith().equals(contact)) {
			Controller.getGui().showChatsWith(contact);
		}
		else {
			// Notify user that a new message has been received
			Controller.getGui().newUnreadMessage(contact);
		}
	}
	
	/** Starts a chat with remote user on given ip*/
	private static TCPConnection startChatWith(InetAddress ip) throws IOException{
		TCPConnection chatConnection = new TCPConnection(ip, TCP_LISTENING_PORT);
		LOGGER.trace("TCPConnection established with " + ip + " on port " + chatConnection.getPort());
		return chatConnection;
	}

	public static void sendMessageHandler(InetAddress ip, String msg) throws IOException {
		TCPConnection chatConnection;
		chatConnection = startChatWith(ip);
		chatConnection.sendMessage(msg);
		LOGGER.info("Sent message: '" + msg + "' to " + ContactList.getInstance().getContact(ip));
		try {
			chatConnection.closeConnection();
		} catch (IOException e) {
			LOGGER.warn("Could not close TCPConnection with " + ip);
		}
	}
}
