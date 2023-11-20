package cs;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class OnJoinHandler extends Thread{
	private Boolean isOnline;
	private UdpListener broadcastListener;
	private ContactList contactList;
	
	public OnJoinHandler(ContactList contactList) {
		this.contactList = contactList;
	}
	
	@Override
	public void run() {
		try {
			broadcastListener = new UdpListener(ContactList.destPort);
			broadcastListener.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isOnline = true;
		// To be running in background while we are logged in
		while(isOnline) {
			while(!broadcastListener.isPacketStackEmpty()) {
				DatagramPacket packet = broadcastListener.popPacketStack();
				String joiningUser = new String(packet.getData(), 0, packet.getLength());
				InetAddress ip = packet.getAddress();
				contactList.getContactDict().put(joiningUser, ip);
				System.out.println( joiningUser + "@"+ ip.toString() + " is now online.");
			}
		}
	}
}
