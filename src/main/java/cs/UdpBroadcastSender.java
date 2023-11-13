package cs;
import java.net.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

public class UdpBroadcastSender extends Thread{
	Enumeration<NetworkInterface> networkinterfaces = NetworkInterface.getNetworkInterfaces();
	List<InetAddress> broadcastAddresseList; // List of all BroadcastAddresses we need to send UDP to
	private int port;
	private DatagramSocket socket;
	private byte[] outBuf = new byte[256];
	
	// Sends and Udp packet on all broadcast addresses possible on port specified in parameter.
	public UdpBroadcastSender(int port) throws SocketException {
		this.port = port;
		socket = new DatagramSocket(8888); // Corresponds to sender socket port used for sending the broadcast, not to be confused with receiving port
		socket.setBroadcast(true); //Enables Broadcasting
	}
	
	@Override
	public void run() {
		// Gets the broadcast addresses from all interfaceAddresses in all the networkInterfaces
		while(networkinterfaces.hasMoreElements()) {
			Iterator<InterfaceAddress> interfaceAddressIter = networkinterfaces.nextElement().getInterfaceAddresses().iterator();
			interfaceAddressIter.forEachRemaining((interfaceAddress) ->
			broadcastAddresseList.add(interfaceAddress.getBroadcast()));
		}
		// Sends a broadcastPacket on all broadcast addresses in broadcastAddresseList
		Iterator<InetAddress> broadCastAddressIter = broadcastAddresseList.iterator();
		broadCastAddressIter.forEachRemaining((broadcastAddress) -> {
			try {
				socket.send(new DatagramPacket(outBuf, outBuf.length, broadcastAddress, port));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
