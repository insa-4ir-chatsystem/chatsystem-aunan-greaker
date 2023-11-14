package cs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Dictionary;
import java.util.Hashtable;

public class UdpReplyListener extends Thread {
	private Dictionary<String, String> replies;
	private DatagramSocket socket;
	private boolean listening;
	
	public UdpReplyListener(int port, int timeoutMS) throws SocketException {
		listening = true;
		replies = new Hashtable<>();
		socket = new DatagramSocket(port);
		socket.setSoTimeout(timeoutMS);
	}
	
	public Dictionary<String, String> getReplies(){
		while (listening) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.replies;
	}
	
		
	@Override
	public void run() {
		while(listening) {
			byte[] buf = new byte[20];
			DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(inPacket); // Blocks until packet received
				replies.put(new String(inPacket.getData(), 0, inPacket.getLength()), inPacket.getAddress().getHostAddress());
			} catch (SocketTimeoutException e) {
				listening = false;
			} catch (IOException e) {
				e.printStackTrace();
				listening = false;
			}
		}
		socket.close();
	}
}
