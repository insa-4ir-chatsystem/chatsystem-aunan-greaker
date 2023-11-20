package cs;
import java.net.SocketException;
import java.util.Scanner;

public class Main {
	private static ContactList contactList;
	public static String username;
	public static void main (String[] args) throws SocketException, InterruptedException {
	    Scanner input = new Scanner(System.in);  // Create a Scanner object
	    System.out.print("Enter username: ");
	    username = input.nextLine();  // Read user input
	    input.close();
	
		contactList = new ContactList(username);
		contactList.makeContactDict();
		System.out.println("Now online with " + contactList.getContactDict().toString());
		System.out.println("Listening for other users that might join...");
		OnJoinHandler onJoinHandlerThread = new OnJoinHandler(contactList);
		onJoinHandlerThread.setDaemon(false);
		onJoinHandlerThread.start();
	}
}
