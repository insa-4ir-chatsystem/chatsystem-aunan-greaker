package cs;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Main {
	private ContactList contactList;
	public static void main (String[] args) throws SocketException, InterruptedException {
	    Scanner input = new Scanner(System.in);  // Create a Scanner object
	    System.out.print("Enter username: ");
	    String username = input.nextLine();  // Read user input
	    input.close();
		
		ContactList contactList = new ContactList(username);
		contactList.makeContactDict();
		System.out.println("Sleeping for 2 seconds...");
		Thread.sleep(2000);
		System.out.println("Online users");
		System.out.println(contactList.getContactDict().toString());
		
		
		
		UdpListener listener = new UdpListener(8888);
		listener.start();
		
		while(listener.isPacketStackEmpty()) {
			Thread.sleep(10);
		}
		
		System.out.println("New user joined!\n Updating contactList...");
		DatagramPacket packet = listener.popPacketStack();
		String newusername = new String(packet.getData(), 0, packet.getLength());
		InetAddress ip = packet.getAddress();
		contactList.getContactDict().put(newusername, ip);
		System.out.println("New contactList: " + contactList.getContactDict().toString());
	}
}
