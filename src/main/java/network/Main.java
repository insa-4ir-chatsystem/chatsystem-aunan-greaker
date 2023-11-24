package network;

import java.net.SocketException;
import java.util.Scanner;

public class Main {
	private static ContactList contactList;
	public static String username;
	public static void main (String[] args) throws SocketException, InterruptedException {
		Boolean validUsername = false;
		Scanner input = new Scanner(System.in);  // Create a Scanner object
		while (!validUsername) {
		    System.out.print("Enter username: ");
		    username = input.nextLine();  // Read user input
		
			contactList = new ContactList(username);
			contactList.makeContactDict();
			validUsername = true;
			for (int i = 0; contactList.getContactDict().size() > i; i++) {
				if (contactList.getAllNames().get(i).equals(username)) {
					validUsername = false;
					System.out.println("The username you chose is already taken! Please choose another username");
				}
			}
		}
	    input.close();
		//Username available test
		System.out.println("Now online with " + contactList.getContactDict().toString());
		System.out.println("Listening for other users that might join...");
		OnJoinHandler onJoinHandlerThread = new OnJoinHandler(contactList);
		onJoinHandlerThread.setDaemon(false);
		onJoinHandlerThread.start();
	}
}
