package cs;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

// Class containing all methods for sending UDP packets, and getting local broadcast addresses
public class UdpSender {	
	private byte[] buf = new byte[256];
	private DatagramSocket socket;
	private InetAddress sendToAddress;
	private int toPort;
	
	public UdpSender(byte[] buf, InetAddress sendToAddress, int toPort, int fromPort) throws SocketException { 
		this.buf = buf;
		this.sendToAddress = sendToAddress;
		this.toPort = toPort;
	    socket = new DatagramSocket(fromPort); 
	}
	
	// The send function can be called after initializing the class to execute the send functionality of the socket to send the message constructed in the instance of the class.
	public void send() throws IOException{
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
	        
	    DatagramPacket packet = new DatagramPacket(buf, buf.length, sendToAddress, toPort);
	    socket.send(packet);
	    socket.close();
	}
	//}
	
	// Gets the broadcast addresses from all interfaceAddresses in all the networkInterfaces
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

