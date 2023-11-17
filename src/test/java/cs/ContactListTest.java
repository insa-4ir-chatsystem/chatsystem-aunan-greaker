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
	public void newEmptyContactListTest() {
		Dictionary<String, String> expected = new Hashtable<>();
		ContactList contactList = new ContactList();
		assertEquals(expected, contactList.getContactDict());
	}
	
	@Test
	public void getNameTest() throws UnknownHostException {
		ContactList contactDict = new ContactList();
		assertEquals(null, contactDict.getName(InetAddress.getLocalHost()));
	}
	
	@Test
	public void getIpTest() {
		ContactList contactDict = new ContactList();
		assertEquals(null, contactDict.getIp("Daniel"));
	}
	
	@Test
	public void addContactTest() throws UnknownHostException {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		String expected = "Idalia : " + InetAddress.getLocalHost().toString();
		assertEquals(expected, contactDict.getName(InetAddress.getLocalHost()) + " : " + contactDict.getIp("Idalia").toString());
	}
	
	@Test
	public void removeContactTest() throws UnknownHostException {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		contactDict.removeContact("Idalia");
		assertEquals(null, contactDict.getName(InetAddress.getLocalHost()));
	}
	
	@Test
	public void getAllNamesTest() throws UnknownHostException {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		contactDict.addContact("Katti", InetAddress.getLocalHost());
		ArrayList<String> expected = new ArrayList<>();
		expected.add("Idalia");
		expected.add("Katti");
		assertEquals(expected, contactDict.getAllNames());
	}
	
	@Test
	public void getAllIpsTest() throws UnknownHostException {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", InetAddress.getLocalHost());
		contactDict.addContact("Katti", InetAddress.getLocalHost());
		ArrayList<InetAddress> expected = new ArrayList<>();
		expected.add(InetAddress.getLocalHost());
		expected.add(InetAddress.getLocalHost());
		assertEquals(expected, contactDict.getAllIps());
	}

}
