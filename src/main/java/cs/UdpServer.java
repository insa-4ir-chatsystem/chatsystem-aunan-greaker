package cs;
import java.net.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

public class UdpServer extends Thread{
	Enumeration<NetworkInterface> networkinterfaces = NetworkInterface.getNetworkInterfaces();
	List<InetAddress> broadcastAddresseList; // List of all BroadcastAddresses we need to send UDP to
	private DatagramSocket socket;
	private byte[] buf = new byte[256];
	
	public UdpServer() throws SocketException {
		socket = new DatagramSocket(8888);
		socket.setBroadcast(true); //Enables Broadcasting
	}
	
	@Override
	public void run() {
		while(networkinterfaces.hasMoreElements()) {
			Iterator<InterfaceAddress> interfaceAddresseIter = networkinterfaces.nextElement().getInterfaceAddresses().iterator(); // Gets an iterator from the list of all interfaceAddresses
			while(interfaceAddresseIter.hasNext()) {
				broadcastAddresseList.add(interfaceAddresseIter.next().getBroadcast()); // Gets the broadcast address of current interfaceAddress
			}
		}
		while(broadcastAddresseList.iterator().hasNext()) {
			DatagramPacket outPacket = new DatagramPacket(buf, buf.length, broadcastAddresseList.iterator().next(), 8888);
			try {
				socket.send(outPacket); // Sends packet from socket
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
