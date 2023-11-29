/*
 * Class that represent an UDP listening socket.
 * UdpListener takes in the port to listen on and a timeoutMS that indicates how long to listen for.
 * Additionally UdpListen can be called without a timeout to run indefinitely.
 * Packet coming into the socket are added to a recievedPacketsStack.
 * The stack can be poped or peeked using popPacketStack or peekPacketStack.
 * 
 * */

package chatsystem.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.EmptyStackException;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class UDPListener extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(UDPListener.class);
	private Stack<UDPMessage> receivedPacketStack;
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
		receivedPacketStack = new Stack<UDPMessage>();
		socket = new DatagramSocket(port);
	}
	
	public UDPListener(int port, int timeoutMS) throws SocketException {
		listening = true;
		receivedPacketStack = new Stack<UDPMessage>();
		socket = new DatagramSocket(port);
		socket.setSoTimeout(timeoutMS);
	}
	
    /** Adds a new observer to the class, for which the handle method will be called for each incoming message. */
    public void addObserver(Observer obs) {
    	// Synchronized to avoid concurrent access
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }
	
	// Pops receivedPacketStack. If stack is empty throws EmptyStackException
	public UDPMessage popPacketStack() throws EmptyStackException {
		return receivedPacketStack.pop();
	}
	
	// Peeks receivedPacketStack. If stack is empty throws EmptyStackException
	public UDPMessage peekPacketStack() throws EmptyStackException {
		return receivedPacketStack.peek();
	}
	
	// true if and only if this stack contains no items; false otherwise.
	public Boolean isPacketStackEmpty() {
		return receivedPacketStack.empty();
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
				
				// Adds message to logger
                LOGGER.trace("Received message on port " + socket.getLocalPort() + ": '" + message.text() + "' from " + message.source());
                
				// Synchronized to avoid concurrent access
		        synchronized (this.observers) {
		        	// Calls handler for all observers
		        	observers.forEach(obs -> obs.handle(message));
		        }
				
				
				/** Put in handler
				receivedPacketStack.push(message);*/
			} catch (SocketTimeoutException e) {
				listening = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		socket.close();
	}
}
