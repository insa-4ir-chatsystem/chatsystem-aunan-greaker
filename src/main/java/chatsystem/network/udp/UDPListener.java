package chatsystem.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/** Class representing a UDP socket that listens for new UDPMessages */
public class UDPListener extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(UDPListener.class);
	private final DatagramSocket SOCKET;
    private final List<Observer> observers = new ArrayList<>();
	
    /** Interface that observers of the UDP server must implement. */
    public interface Observer {
        /** Method that is called each time a message is received. */
        void handle(UDPMessage message);
    }
	
	/**	Constructor for socket running indefinitely */
	public UDPListener(int port) throws SocketException {
		SOCKET = new DatagramSocket(port);
	}
	
	/**	Constructor for socket running for a limited time */
	public UDPListener(int port, int timeoutMS) throws SocketException {
		SOCKET = new DatagramSocket(port);
		SOCKET.setSoTimeout(timeoutMS);
	}
	
    /** Adds a new observer to the class, for which the handle method will be called for each incoming message. */
    public synchronized void addObserver(Observer obs) {
        this.observers.add(obs);
    }

	/** Closes the socket */
	public void close() {
		SOCKET.close();
	}
	
	@Override
	public void run() {
		LOGGER.trace("UDPListener started on port " + SOCKET.getLocalPort());
		while(!SOCKET.isClosed()) {
			byte[] buf = new byte[200];
			DatagramPacket incomingPacket = new DatagramPacket(buf, buf.length);
			try {
				// Waits for the next message
				SOCKET.receive(incomingPacket);
				
				// Extracts message 
				String received = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
				UDPMessage message = new UDPMessage(received, incomingPacket.getAddress());
				
				// Ignore messages coming from one of its own NIC addresses
				if (UDPSender.getAllCurrentIp().contains(message.source())) {
					LOGGER.trace("Ignored message from LocalHost " + SOCKET.getLocalPort() + ": '" + message.text() + "' from " + message.source());
					continue;
				}
				
				// Adds message to logger
                LOGGER.trace("Received message on port " + SOCKET.getLocalPort() + ": '" + message.text() + "' from " + message.source());
                
				// Synchronized to avoid concurrent access
		        synchronized (this.observers) {
		        	// Calls handler for all observers
		        	observers.forEach(obs -> obs.handle(message));
		        }
				
			} catch (SocketTimeoutException e) {
				LOGGER.trace("UDPListener timed out");
				close();
			} catch (IOException e) {
				if (!SOCKET.isClosed()) {
					LOGGER.error("Could not receive packet: " + e.getMessage());
				}
			}
		}
		LOGGER.trace("UDPListener closed");
	}
}
