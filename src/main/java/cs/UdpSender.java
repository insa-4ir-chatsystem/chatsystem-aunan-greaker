package cs;

import java.io.IOException;
import java.net.*;

public class UdpSender {	
	private byte[] buf = new byte[256];
	private DatagramSocket socket;
	private InetAddress sendToAddress;
	private int port;
	
	public UdpSender(byte[] buf, InetAddress sendToAddress, int port) throws SocketException { 
		this.buf = buf;
		this.sendToAddress = sendToAddress;
		this.port = port;
	    socket = new DatagramSocket(port); 
	}
	    
	public void send() {
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
	        
	        DatagramPacket outPacket = new DatagramPacket(buf, buf.length, sendToAddress, port);
	        try {
				socket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	//}
}

