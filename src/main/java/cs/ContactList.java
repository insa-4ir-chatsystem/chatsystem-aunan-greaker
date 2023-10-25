package cs;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class ContactList {
    private Dictionary<String, String> contactDict;

    public ContactList() {
        contactDict = new Hashtable<>();

        //Step 1: Send UDP broacast to network

        //Step 2: Listen to response and add replies to contactDict
        
    }

    public void updateContactList() {
        //Step 1: Send UDP broacast to network

        //Step 2: Listen to response and add replies to contactDict
    }

    public void addContact(String name, String ip) {
        contactDict.put(name, ip);
    }

    public void removeContact(String name) {
        contactDict.remove(name);
    }

    public ArrayList<String> getAllNames() {
        ArrayList<String> names = new ArrayList<>();
        Enumeration<String> k = contactDict.keys();
        while (k.hasMoreElements()) {
            names.add(k.nextElement());
        }
        return names;
    }

    public ArrayList<String> getAllIPs() {
        ArrayList<String> ips = new ArrayList<>();
        Enumeration<String> k = contactDict.elements();
        while (k.hasMoreElements()) {
            ips.add(k.nextElement());
        }
        return ips;
    }

    public String getIP(String name) {
        return contactDict.get(name);
    }

    public String getName(String ip) {
        Enumeration<String> k = contactDict.keys();
        while (k.hasMoreElements()) {
            String key = k.nextElement();
            if (contactDict.get(key).equals(ip)) {
                return key;
            }
        }
        return null;
    }

}
