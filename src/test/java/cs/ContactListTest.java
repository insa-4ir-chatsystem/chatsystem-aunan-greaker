package cs;

import static org.junit.Assert.*;
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
	public void getNameTest() {
		ContactList contactDict = new ContactList();
		assertEquals(null, contactDict.getName("localhost"));
	}
	
	@Test
	public void getIpTest() {
		ContactList contactDict = new ContactList();
		assertEquals(null, contactDict.getIp("Daniel"));
	}
	
	@Test
	public void addContactTest() {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", "192.168.0.1");
		String expected = "Idalia : 192.168.0.1";
		assertEquals(expected, contactDict.getName("192.168.0.1") + " : " + contactDict.getIp("Idalia"));
	}
	
	@Test
	public void removeContactTest() {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", "192.168.0.1");
		contactDict.removeContact("Idalia");
		assertEquals(null, contactDict.getName("192.168.0.1"));
	}
	
	@Test
	public void getAllNamesTest() {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", "192.168.0.1");
		contactDict.addContact("Katti", "192.168.0.2");
		ArrayList<String> expected = new ArrayList<>();
		expected.add("Idalia");
		expected.add("Katti");
		assertEquals(expected, contactDict.getAllNames());
	}
	
	@Test
	public void getAllIpsTest() {
		ContactList contactDict = new ContactList();
		contactDict.addContact("Idalia", "192.168.0.1");
		contactDict.addContact("Katti", "192.168.0.2");
		ArrayList<String> expected = new ArrayList<>();
		expected.add("192.168.0.1");
		expected.add("192.168.0.2");
		assertEquals(expected, contactDict.getAllIps());
	}

}
