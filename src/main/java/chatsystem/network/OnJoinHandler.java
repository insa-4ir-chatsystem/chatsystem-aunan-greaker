/*	This class extends a Thread and runs in the background listening for joining users on the broadcastPort.
 * 	When a user joins it adds the users to its contactList and replies to the user so that the new user will know who is online.
 * 
 * */

package chatsystem.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import chatsystem.Main;

public class OnJoinHandler extends Thread{
	private Boolean isOnline;
	private UDPListener broadcastListener;
	private ContactList contactList;
	
	public OnJoinHandler(ContactList contactList) {
		this.contactList = contactList;
	}
	
	@Override
	public void run() {
		try {
			broadcastListener = new UDPListener(ContactList.broadcastPort);
			broadcastListener.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isOnline = true;
		// To be running in background while we are logged in
		while(isOnline) {
			while(!broadcastListener.isPacketStackEmpty()) {
				// Adds joining users to contactList
				UDPMessage message = broadcastListener.popPacketStack();
				String joiningUser = message.text();
				InetAddress ip = message.source();
				contactList.addContact(joiningUser, ip);
				System.out.println( joiningUser + " is now online (ip: " + ip.toString() + ") \n");
				System.out.println("Listening for other users that might join...");
				
				// Replies to Udp broadcast
				try {
					UDPSender.send(ip, ContactList.broadcastReplyPort, Main.username);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
