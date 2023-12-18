package chatsystem.network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.Main;
import chatsystem.contacts.ContactList.Observer;

/** Class containing all methods for sending UDP packets, and getting local broadcast addresses */
public class UDPSender {	
	
	 private static final Logger LOGGER = LogManager.getLogger(UDPSender.class);
	
    /** Sends a UDP message on the given address and port. */
    public static void send(InetAddress addr, int port, String message) throws IOException {
        DatagramSocket socket = new DatagramSocket(); // Will create a socket on an available port
        byte[] buff = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, addr, port);
        socket.send(packet);
        socket.close();
    }
	
	// Sends the message buf on all local broadcast addresses found in the getAllLocalBroadcastAddresses function of this class
	public static void sendBroadcast(int port, String msg) throws IOException {
		// TODO
		ArrayList<InetAddress> broadcastAddresses = getAllLocalBroadcastAddresses();
        for (InetAddress broadAddr : broadcastAddresses) {
        	LOGGER.info("Found this broadcast address: " + broadAddr);
        	if (!broadAddr.equals(InetAddress.getByName("127.255.255.255"))) {
        		send(broadAddr, port, msg);
        	}
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

