package cs;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class ContactList {
    private Dictionary<String, String> contactDict;

    public ContactList() {
        contactDict = new Hashtable<>();
        this.updateContactList();
        
    }

    public void updateContactList() {
        //Step 1: Send UDP broadcast to network
    		// All Connected users should reply with their username and ip
    	try {
			(new UdpBroadcastSender(8888)).start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //Step 2: Listen to response and add replies to contactDict
    	try {
			UdpReplyListener listener = new UdpReplyListener(8888, 1000);
			listener.getUsers(); // Sleeps until ReplyListener socket timesout
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

    public ArrayList<String> getAllIps() {
        ArrayList<String> ips = new ArrayList<>();
        Enumeration<String> k = contactDict.elements();
        while (k.hasMoreElements()) {
            ips.add(k.nextElement());
        }
        return ips;
    }

    public String getIp(String name) {
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
