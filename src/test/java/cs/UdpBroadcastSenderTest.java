package cs;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import org.junit.Test;

public class UdpBroadcastSenderTest {

	@Test
	public void zeroBroadcastSentTest() throws SocketException {
		Dictionary<String, String> expected = new Hashtable<>();
		UdpReplyListener listener = new UdpReplyListener(8888, 1000);
		listener.start();
		assertEquals(expected, listener.getReplies());
	}
	
	@Test
	public void oneBroadcastSentTest() throws SocketException, UnknownHostException {
		List<Dictionary<String, String>> expectedList = new ArrayList<Dictionary<String, String>>(); // List of all possible Dictionary results
		
		// Gets all broadcastAddresses and adds them to expected
		Enumeration<NetworkInterface> networkinterfaces = NetworkInterface.getNetworkInterfaces();
		while(networkinterfaces.hasMoreElements()) {
			Iterator<InterfaceAddress> interfaceAddressIter = networkinterfaces.nextElement().getInterfaceAddresses().iterator();
			interfaceAddressIter.forEachRemaining((interfaceAddress) -> {
				Dictionary<String, String> expected = new Hashtable<>();
				try {
					expected.put("BroadcastMsg", interfaceAddress.getAddress().getHostAddress());
					expectedList.add(expected);
				} catch (NullPointerException e) {
					// getBroadcast returned null most likely because interface is IPv6, getHostAddress throws NullPointerException
				}
			});
		}
		UdpReplyListener listener = new UdpReplyListener(8888, 100);
		listener.start();
		new UdpBroadcastSender("BroadcastMsg", 8888).start();
		assertTrue(expectedList.contains(listener.getReplies()));
	}

}
