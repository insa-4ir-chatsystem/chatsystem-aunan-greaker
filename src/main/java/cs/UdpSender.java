package cs;

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
	public void send(byte[] buf, InetAddress sendToAddress) throws IOException{
	    /*while (true) {
	        DatagramPacket packet  = new DatagramPacket(buf, buf.length);        
	        try {
	        	System.out.println("Waiting for packet");
				socket.receive(inPacket);
				
				System.out.println("Packet recieved!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String received = new String(inPacket.getData(), 0, inPacket.getLength());
	        System.out.println("Received: " + received);
	        */
	        
		DatagramSocket socket = new DatagramSocket(fromPort);
		socket.setBroadcast(true);
	    DatagramPacket packet = new DatagramPacket(buf, buf.length, sendToAddress, toPort);
	    socket.send(packet);
	    socket.close();
	}
	//}
	
	// Sends the message buf on all local broadcast addresses found in the getAllLocalBroadcastAddresses function of this class
	public void sendBroadcast(byte[] buf) throws IOException {
		ArrayList<InetAddress> broadcastAddresses = getAllLocalBroadcastAddresses();
		while (!broadcastAddresses.isEmpty()) {
			send(buf, broadcastAddresses.get(0));
		}
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

