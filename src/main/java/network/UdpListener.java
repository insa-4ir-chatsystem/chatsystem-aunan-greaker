/*
 * Class that represent an UDP listening socket.
 * UdpListener takes in the port to listen on and a timeoutMS that indicates how long to listen for.
 * Additionally UdpListen can be called without a timeout to run indefinitely.
 * Packet coming into the socket are added to a recievedPacketsStack.
 * The stack can be poped or peeked using popPacketStack or peekPacketStack.
 * 
 * */

package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.EmptyStackException;
import java.util.Stack;

public class UdpListener extends Thread {
	private Stack<DatagramPacket> receivedPacketStack;
	private DatagramSocket socket;
	private boolean listening;
	private int port;
	
	public UdpListener(int port) throws SocketException {
		this.port = port;
		listening = true;
		receivedPacketStack = new Stack<DatagramPacket>();
		socket = new DatagramSocket(port);
	}
	
	public UdpListener(int port, int timeoutMS) throws SocketException {
		listening = true;
		receivedPacketStack = new Stack<DatagramPacket>();
		socket = new DatagramSocket(port);
		socket.setSoTimeout(timeoutMS);
	}
	
	// Pops receivedPacketStack. If stack is empty throws EmptyStackException
	public DatagramPacket popPacketStack() throws EmptyStackException {
		return receivedPacketStack.pop();
	}
	
	// Peeks receivedPacketStack. If stack is empty throws EmptyStackException
	public DatagramPacket peekPacketStack() throws EmptyStackException {
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
			byte[] buf = new byte[20];
			DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
			try {
				System.out.println("listening on port: " + port);
				socket.receive(inPacket); // Blocks until packet received
				receivedPacketStack.push(inPacket);
				System.out.println("Received packet from: " + inPacket.getAddress().toString());
			} catch (SocketTimeoutException e) {
				listening = false;
			} catch (IOException e) {
				e.printStackTrace();
				listening = false;
			}
		}
		socket.close();
	}
}
