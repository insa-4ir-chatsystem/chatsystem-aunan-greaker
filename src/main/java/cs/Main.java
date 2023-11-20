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
		
		System.out.println("Creating contactList object from username: " + username + "...");
		ContactList contactList = new ContactList(username);
		System.out.println("Created contactList object susccesfully");
		System.out.println("Running makeContactDict...");
		contactList.makeContactDict();
		System.out.println("Sleeping for 5 seconds...");
		Thread.sleep(5000);
		System.out.println("Accessing contactDict...");
		System.out.println(contactList.getContactDict().toString());
		
		System.out.println("Now online");
		System.out.println("Listening for other users...");
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
