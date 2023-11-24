/*
 * The ContactList class handles the phase of making a contactList of all online users using the chatsystem on the local network.
 * The ContactList is stored as a dictionary in contactDict that can be accessed from the outside using getContactList.
 * The contactList can also be partially accessed using getName(), getAllNames(), getIp(), getAllIps().
 * */

package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class ContactList {
	public static final int broadcastPort = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final int broadcastReplyPort = 7472; // The port to reply to when receiving a broadcast.
	public static final int srcPort = 7473; // The port sending the broadcast
    private Dictionary<String, InetAddress> contactDict;
    private String username;

    public ContactList(String username) {
    	this.username = username;
        contactDict = new Hashtable<>();
    }

    /* Creates a contactDict by sending an UDP broadcast to destPort and listening to the responses on the srcPort.
     * 
     */
    public void makeContactDict() throws SocketException, InterruptedException {
    	
        //Listen for responses
    	UdpListener listener = new UdpListener(broadcastReplyPort, 5000);
		listener.start();
		Thread.sleep(500); // Short Sleep to make sure socket is listening
    	
        //Send UDP broadcast to network
    	UdpSender sender = new UdpSender(broadcastPort, srcPort);
    	try {
			sender.sendBroadcast(username.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	while(listener.isAlive()) {} // Waits for listener to timeout
		// While there are packets in the stack pops them and adds them to contactList.
		while(!listener.isPacketStackEmpty()) {
			DatagramPacket packet = listener.popPacketStack();
			String username = new String(packet.getData(), 0, packet.getLength());
			InetAddress ip = packet.getAddress();
			contactDict.put(username, ip);
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

    public String getName(InetAddress ip) {
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
