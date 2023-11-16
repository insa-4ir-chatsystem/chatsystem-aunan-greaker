package cs;

import java.io.IOException;
import java.net.*;

public class UdpListener {	
	public UdpListener(String username) throws SocketException { 
	    byte[] buf = new byte[256];
	    DatagramSocket socket = new DatagramSocket(8888);
	    while (true) {
	        DatagramPacket inPacket  = new DatagramPacket(buf, buf.length);
	        try {
				socket.receive(inPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String received = new String(inPacket.getData(), 0, inPacket.getLength());
	        System.out.println("Received: " + received);
	        
	        InetAddress senderAddress = inPacket.getAddress();
	        int senderPort = inPacket.getPort();
	        DatagramPacket outPacket = new DatagramPacket(ChooseUsernameGUI.myUsername.getBytes(), ChooseUsernameGUI.myUsername.getBytes().length, senderAddress, senderPort);
	        try {
				socket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}

