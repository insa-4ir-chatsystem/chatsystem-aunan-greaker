package cs;

import java.io.IOException;
import java.net.*;
import java.util.Iterator;


// An instance of this class should consist of a byte[] containing the message to be sent, the InetAddress of the message IP destination, and the number of the port on which the message should be sent to.
// This class consists of a constructor and a function 'send' which constructs the datagrampacket containing the information provided in the class construction, and sends it on the datagramsocket 'socket', defined in the class constructor.
public class UdpSender {	
	private byte[] buf = new byte[256];
	private DatagramSocket socket;
	private InetAddress sendToAddress;
	private int toPort;
	
	public UdpSender(byte[] buf, InetAddress sendToAddress, int toPort, int sendPort) throws SocketException { 
		this.buf = buf;
		this.sendToAddress = sendToAddress;
		this.toPort = toPort;
	    socket = new DatagramSocket(sendPort); 
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
	}
	//}
	
	// Gets the broadcast addresses from all interfaceAddresses in all the networkInterfaces
	/*public static getAllLocalBroadcastAddresses() {
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
		}*/
	
}

