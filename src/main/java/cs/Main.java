package cs;
import java.net.SocketException;
import java.util.Scanner;

public class Main {
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
	}
}
