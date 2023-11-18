package cs;

import java.net.SocketException;

public class Main {
	public static void main (String[] args) throws SocketException, InterruptedException {
		System.out.println("Creating contactList object...");
		ContactList contactList = new ContactList();
		System.out.println("Created contactList object susccesfully");
		System.out.println("Running makeContactDict...");
		contactList.makeContactDict();
		System.out.println("Sleeping for 5 seconds...");
		Thread.sleep(5000);
		System.out.println("Accessing contactDict...");
		System.out.println(contactList.getContactDict().toString());
	}
}
