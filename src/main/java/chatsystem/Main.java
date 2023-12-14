package chatsystem;
import java.net.SocketException;
import java.util.Scanner;

import chatsystem.contacts.ContactList;
import chatsystem.network.*;

public class Main {
	/*private static ContactList contactList;
	public static String username;*/
	public static void main (String[] args) throws SocketException, InterruptedException {
		new chatsystem.gui.ChooseUsernameGUI();
		// TODO
		/*Boolean validUsername = false;
		Scanner input = new Scanner(System.in);*/  // Create a Scanner object
		// TODO
		/*while (!validUsername) {
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
		}*/
	    /*input.close();
		System.out.println("Now online with " + contactList.getAllContacts().toString());
		System.out.println("Listening for other users that might join...");
		OnJoinHandler onJoinHandlerThread = new OnJoinHandler(contactList);
		onJoinHandlerThread.setDaemon(false);
		onJoinHandlerThread.start();*/
	}
}
