import java.util.ArrayList;

import cs.ContactList;

public class TestContactList {
    public static void main(String[] args) {
        ContactList contactList = new ContactList();
        contactList.addContact("Idalia", "192.168.0.1");
        contactList.addContact("Daniel", "localhost");
        contactList.addContact("Katti", "192.168.0.2");
        contactList.addContact("Luri", "192.168.0.3");
        testGetAllNames(contactList);
        testGetAllIPs(contactList);
    }

    public static void testGetAllNames(ContactList contactList) {
        System.out.println("Testing getAllNames...");
        ArrayList<String> names = contactList.getAllNames();
        for (String name : names) {
            System.out.print(name + " ");
        }
    }

    public static void testGetAllIPs(ContactList contactList) {
        System.out.println("Testing getAllIPs...");
        ArrayList<String> ips = contactList.getAllIPs();
        for (String ip : ips) {
            System.out.print(ip + " ");
        }
    }
}
