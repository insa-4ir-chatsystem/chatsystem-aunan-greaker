package cs;

import static org.junit.Assert.*;

import java.net.SocketException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

public class UdpReplyListenerTest {

	@Test
	public void zeroReplyTest() {
		Dictionary<String, String> expected = new Hashtable<>();
		UdpReplyListener listener = null;
		try {
			listener = new UdpReplyListener(8888);
			listener.quit();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			fail(e.toString());
		}
		assertEquals(expected, listener.getUsers());
	}
	
	@Test
	public void oneReplyTest() {
		Dictionary<String, String> expected = new Hashtable<>();
		expected.put("Daniel", "127.0.0.1");
		UdpReplyListener listener = null;
		try {
			listener = new UdpReplyListener(8888);
			Thread.sleep(5000);
			listener.quit();
		} catch (SocketException | InterruptedException e) {
			// TODO Auto-generated catch block
			fail(e.toString());
		}
		assertEquals(expected, listener.getUsers());
	}

}
