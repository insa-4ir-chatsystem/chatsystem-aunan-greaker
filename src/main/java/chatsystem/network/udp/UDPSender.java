package chatsystem.network.udp;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/** Class containing all methods for sending UDP packets, and getting local broadcast addresses */
public class UDPSender {	
	private static final Logger LOGGER = LogManager.getLogger(UDPSender.class);
	
    /** Sends a UDP message on the given address and port. */
    public static void send(InetAddress addr, int port, String message) throws IOException, NullPointerException {
        DatagramSocket socket = new DatagramSocket(); // Will create a socket on an available port
        socket.setBroadcast(true);
        byte[] buff = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, addr, port);
        socket.send(packet);
        socket.close();
    }
	
	/** Sends the message on all broadcast addresses found on the system*/
	public static void sendBroadcast(int port, String msg) throws IOException {
		ArrayList<InetAddress> broadcastAddresses = getAllBroadcastAddresses();
        for (InetAddress broadAddr : broadcastAddresses) {
        	send(broadAddr, port, msg);
        }
	}
	
	/** Gets the broadcast addresses from all networks the system is connected to*/
	public static ArrayList<InetAddress> getAllBroadcastAddresses() {
		
    	ArrayList<InetAddress> AllBroadcastIp = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            LOGGER.error("Unable to get network interfaces " + e.getMessage(), e);
            return null;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
            List<InterfaceAddress> nias = ni.getInterfaceAddresses();
            for (InterfaceAddress ia : nias) {
            	InetAddress broadAddr = ia.getBroadcast();
            	if (broadAddr != null
            	&&(!broadAddr.isLinkLocalAddress())
                && (!broadAddr.isLoopbackAddress())
                && (broadAddr instanceof Inet4Address)) {
            		
            		AllBroadcastIp.add(broadAddr);
                }
            }
        }
      return AllBroadcastIp;
	}
	
	/** Returns all current IPv4 addresses the system has */
	public static ArrayList<InetAddress> getAllCurrentIp() {
        try {
        	ArrayList<InetAddress> AllCurrentIp = new ArrayList<>();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while(nias.hasMoreElements()) {
                    InetAddress ia= (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress() 
                     && !ia.isLoopbackAddress()
                     && ia instanceof Inet4Address) {
                        AllCurrentIp.add(ia);
                    }
                }
            }
            return AllCurrentIp;
        } catch (SocketException e) {
            LOGGER.error("unable to get current IPs " + e.getMessage(), e);
        }
        return null;
    }
}

