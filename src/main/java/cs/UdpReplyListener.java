package cs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Dictionary;
import java.util.Hashtable;

public class UdpReplyListener extends Thread {
	private Dictionary<String, String> users;
	private DatagramSocket socket;
	private boolean listening;
	
	public UdpReplyListener(int port, int timeoutMS) throws SocketException {
		listening = true;
		users = new Hashtable<>();
		socket = new DatagramSocket(port);
		socket.setSoTimeout(timeoutMS);
	}
	
	public Dictionary<String, String> getUsers(){
		while (listening) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return users;
	}
	
		
	@Override
	public void run() {
		while(listening) {
			byte[] buf = new byte[500];
			DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
			try {
				System.out.println("Listening for packet");
				socket.receive(inPacket); // Blocks until packet received
				System.out.println("Packet received");
				users.put(inPacket.getData().toString(), inPacket.getAddress().toString());
			} catch (SocketTimeoutException e) {
				System.out.println("SocketTimedout");
				listening = false;
			} catch (IOException e) {
				e.printStackTrace();
				listening = false;
			}
			
			
		}
		socket.close();
		System.out.println("Socket closed");
	}
}
