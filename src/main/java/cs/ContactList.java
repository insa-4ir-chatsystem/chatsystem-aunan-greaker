package cs;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class ContactList {
    private Dictionary<String, InetAddress> contactDict;

    public ContactList() {
        contactDict = new Hashtable<>();
        this.updateContactDict();
        
    }

    public void updateContactDict() {
        //Step 1: Send UDP broadcast to network
    		// All Connected users should reply with their username and ip
    	UdpSender sender = new UdpSender(8888, 8889);
    	sender.sendBroadcast("BroadcastMsg".getBytes());

        //Step 2: Listen to response and add replies to contactDict
    	try {
			UdpListener listener = new UdpListener(8889, 1000);
			listener.start();
			
			// While there are packets in the stack pops them and adds them to contactList.
			while(!listener.isPacketStackEmpty()) {
				DatagramPacket packet = listener.popPacketStack();
				String username = new String(packet.getData(), 0, packet.getLength());
				InetAddress ip = packet.getAddress();
				contactDict.put(username, ip);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    public void addContact(String name, InetAddress ip) {
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

    public ArrayList<InetAddress> getAllIps() {
        ArrayList<InetAddress> ips = new ArrayList<>();
        Enumeration<InetAddress> k = contactDict.elements();
        while (k.hasMoreElements()) {
            ips.add(k.nextElement());
        }
        return ips;
    }
    
    public Dictionary<String, InetAddress> getContactDict(){
    	return this.contactDict;
    }

    public InetAddress getIp(String name) {
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
