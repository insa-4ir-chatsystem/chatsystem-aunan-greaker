/*
 * Class that represent an UDP listening socket.
 * UdpListener takes in the port to listen on and a timeoutMS that indicates how long to listen for.
 * Additionally UdpListen can be called without a timeout to run indefinitely.
 * Packet coming into the socket are added to a recievedPacketsStack.
 * The stack can be poped or peeked using popPacketStack or peekPacketStack.
 * 
 * */

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

public class UDPListener extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(UDPListener.class);
	private final DatagramSocket socket;
    private final List<Observer> observers = new ArrayList<>();
	private boolean listening;
	
    /** Interface that observers of the UDP server must implement. */
    public interface Observer {
        /** Method that is called each time a message is received. */
        void handle(UDPMessage message);
    }
	
	public UDPListener(int port) throws SocketException {
		listening = true;
		socket = new DatagramSocket(port);
	}
	
	public UDPListener(int port, int timeoutMS) throws SocketException {
		listening = true;
		socket = new DatagramSocket(port);
		socket.setSoTimeout(timeoutMS);
	}
	
    /** Adds a new observer to the class, for which the handle method will be called for each incoming message. */
    public synchronized void addObserver(Observer obs) {
        this.observers.add(obs);
    }
	
	public Boolean isListening() {
		return listening;
	}
	
		
	@Override
	public void run() {
		while(listening) {
			byte[] buf = new byte[200];
			DatagramPacket incomingPacket = new DatagramPacket(buf, buf.length);
			try {
				// Waits for the next message
				socket.receive(incomingPacket);
				
				// Extracts message 
				String received = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
				UDPMessage message = new UDPMessage(received, incomingPacket.getAddress());
				
				LOGGER.trace(message.source().getHostAddress());
				// Ignore messages on from LocalHost
				if (UDPSender.getAllCurrentIp().contains(message.source())) {
					LOGGER.trace("Ignored message from LocalHost " + socket.getLocalPort() + ": '" + message.text() + "' from " + message.source());
					continue;
				}
				
				// Adds message to logger
                LOGGER.trace("Received message on port " + socket.getLocalPort() + ": '" + message.text() + "' from " + message.source());
                
				// Synchronized to avoid concurrent access
		        synchronized (this.observers) {
		        	// Calls handler for all observers
		        	observers.forEach(obs -> obs.handle(message));
		        }
				
			} catch (SocketTimeoutException e) {
				listening = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		socket.close();
	}
}
