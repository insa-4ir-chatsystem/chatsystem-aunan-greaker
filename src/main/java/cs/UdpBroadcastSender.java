package cs;
import java.net.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class UdpBroadcastSender extends Thread{
	Enumeration<NetworkInterface> networkinterfaces = NetworkInterface.getNetworkInterfaces();
	private int port;
	private DatagramSocket socket;
	private byte[] outBuf;
	
	// Sends and Udp packet with msg on all broadcast addresses possible on port specified in parameter.
	public UdpBroadcastSender(String msg, int port) throws SocketException {
		this.port = port;
		this.outBuf = msg.getBytes();
		socket = new DatagramSocket(9999); // Corresponds to sender socket port used for sending the broadcast, not to be confused with receiving port
		socket.setBroadcast(true); //Enables Broadcasting
	}
	
	@Override
	public void run() {
		// Gets the broadcast addresses from all interfaceAddresses in all the networkInterfaces
		while(networkinterfaces.hasMoreElements()) {
			Iterator<InterfaceAddress> interfaceAddressIter = networkinterfaces.nextElement().getInterfaceAddresses().iterator();
			interfaceAddressIter.forEachRemaining((interfaceAddress) -> {
				try {
					socket.send(new DatagramPacket(outBuf, outBuf.length, interfaceAddress.getBroadcast(), port));
				} catch (IllegalArgumentException e) {
					// Broadcast address likely was null because of IPv6 an interface
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		socket.close();
	}
}
