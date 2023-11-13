package cs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Dictionary;
import java.util.Hashtable;

public class UdpReplyListener extends Thread {
	private Dictionary<String, String> users;
	private DatagramSocket socket;
	private boolean listening;
	
	public UdpReplyListener(int port) throws SocketException {
		listening = true;
		users = new Hashtable<>();
		socket = new DatagramSocket(port);
	}
	
	public Dictionary<String, String> getUsers(){
		return users;
	}
	
	public void quit() {
		listening = false;
		socket.close();
	}
	
		
	@Override
	public void run() {
		while(listening) {
			byte[] buf = new byte[256];
			DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(inPacket); // Blocks until packet received
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			users.put(inPacket.getData().toString(), inPacket.getAddress().toString());
			
		}
	}
}
