package cs;

import java.io.IOException;
import java.net.*;

public class UdpBroadcastListener extends Thread{	
	private String username;
	byte[] buf = new byte[256];
	private DatagramSocket socket;
	
	public UdpBroadcastListener(String username, int port) throws SocketException { 
		this.username = username;
	    socket = new DatagramSocket(port); 
	}
	    
	@Override
	public void run() {
	    while (true) {
	        DatagramPacket inPacket  = new DatagramPacket(buf, buf.length);
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
	        
	        InetAddress senderAddress = inPacket.getAddress();
	        int senderPort = inPacket.getPort();
	        DatagramPacket outPacket = new DatagramPacket(username.getBytes(), username.getBytes().length, senderAddress, senderPort);
	        try {
				socket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}

