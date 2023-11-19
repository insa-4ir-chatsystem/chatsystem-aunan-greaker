package cs;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

public class ContactListTest {

	@Test
	public void newEmptyContactListTest() throws InterruptedException {
		Dictionary<String, String> expected = new Hashtable<>();
		ContactList contactList = new ContactList("user");
		assertEquals(expected, contactList.getContactDict());
		Thread.sleep(100); // Sleep added to give socket enough time to close
	}
	
	@Test
	public void getNameTest() throws UnknownHostException, InterruptedException {
		ContactList contactDict = new ContactList("user");
		assertEquals(null, contactDict.getName(InetAddress.getLocalHost()));
		Thread.sleep(100);
	}
	
	@Test
	public void getIpTest() throws InterruptedException {
		ContactList contactDict = new ContactList("user");
		assertEquals(null, contactDict.getIp("Daniel"));
		Thread.sleep(100);
	}
	
	@Test
	public void addContactTest() throws UnknownHostException, InterruptedException {
		ContactList contactDict = new ContactList("user");
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		String expected = "Idalia : " + InetAddress.getLocalHost().toString();
		assertEquals(expected, contactDict.getName(InetAddress.getLocalHost()) + " : " + contactDict.getIp("Idalia").toString());
		Thread.sleep(100);
	}
	
	@Test
	public void removeContactTest() throws UnknownHostException, InterruptedException {
		ContactList contactDict = new ContactList("user");
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		contactDict.removeContact("Idalia");
		assertEquals(null, contactDict.getName(InetAddress.getLocalHost()));
		Thread.sleep(100);
	}
	
	@Test
	public void getAllNamesTest() throws UnknownHostException, InterruptedException {
		ContactList contactDict = new ContactList("user");
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		contactDict.addContact("Katti", InetAddress.getLocalHost());
		ArrayList<String> expected = new ArrayList<>();
		expected.add("Idalia");
		expected.add("Katti");
		assertEquals(expected, contactDict.getAllNames());
		Thread.sleep(100);
	}
	
	@Test
	public void getAllIpsTest() throws UnknownHostException, InterruptedException {
		ContactList contactDict = new ContactList("user");
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		contactDict.addContact("Katti", InetAddress.getLocalHost());
		ArrayList<InetAddress> expected = new ArrayList<>();
		expected.add(InetAddress.getLocalHost());
		expected.add(InetAddress.getLocalHost());
		assertEquals(expected, contactDict.getAllIps());
		Thread.sleep(100);
	}

}
