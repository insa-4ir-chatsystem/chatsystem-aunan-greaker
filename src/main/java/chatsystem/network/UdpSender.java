package chatsystem.network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

// Class containing all methods for sending UDP packets, and getting local broadcast addresses
public class UdpSender {	
	private int toPort;
	private int fromPort;
	
	public UdpSender(int toPort, int fromPort) { 
		this.toPort = toPort;
		this.fromPort = fromPort; 
	}
	
	// The send function can be called after initializing the class to execute the send functionality of the socket to send the message constructed in the instance of the class.
	public void send(byte[] buf, InetAddress destinationAddr) throws IOException{  
		DatagramSocket socket = new DatagramSocket(fromPort);
		socket.setBroadcast(true);
	    DatagramPacket packet = new DatagramPacket(buf, buf.length, destinationAddr, toPort);
	    socket.send(packet);
	    socket.close();
	}
	
	// Sends the message buf on all local broadcast addresses found in the getAllLocalBroadcastAddresses function of this class
	public void sendBroadcast(byte[] buf) throws IOException {
		ArrayList<InetAddress> broadcastAddresses = getAllLocalBroadcastAddresses();
		broadcastAddresses.forEach((broadAddr) -> {
			try {
				send(buf, broadAddr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	// Gets the local broadcast addresses from all interfaceAddresses in all the networkInterfaces, and adds them to an arraylist that is returned at the end of the function
	public static ArrayList<InetAddress> getAllLocalBroadcastAddresses() throws SocketException {
		
		ArrayList<InetAddress> returnList = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> networkinterfaces = NetworkInterface.getNetworkInterfaces();
		
		while(networkinterfaces.hasMoreElements()) {
			Iterator<InterfaceAddress> interfaceAddressIter = networkinterfaces.nextElement().getInterfaceAddresses().iterator();
			interfaceAddressIter.forEachRemaining((interfaceAddress) -> {
				if (interfaceAddress.getBroadcast() != null) {
					returnList.add(interfaceAddress.getBroadcast());
				}
			});
		}
		return returnList;
	}
}

