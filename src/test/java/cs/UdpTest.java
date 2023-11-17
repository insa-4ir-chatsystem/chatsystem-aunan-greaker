package cs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;

public class UdpTest {

	@Test
	public void UdpSenderTest() {
		try {
			UdpSender sender = new UdpSender("This is a test".getBytes(), InetAddress.getLocalHost(), 8888, 9999);
			try {
				sender.send();
			} catch (IOException e) {
				fail("Failed with IOException: " + e);
			}
		} catch (SocketException e) {
			fail("Failed with SocketException: " + e);
		} catch (UnknownHostException e) {
			fail("Failed with UnknownHostException: " + e);
		}
	}

	@Test
	public void UdpListenerTest() throws SocketException, InterruptedException {
		UdpListener listener = new UdpListener(8888, 100);
		listener.start();
		try {
			UdpSender sender = new UdpSender("TestPacket".getBytes(), InetAddress.getLocalHost(), 8888, 9999);
			sender.send();
		} catch (IOException e) {
			fail("Failed because of UdpSender. " + e);
		}
		Thread.sleep(100);
		assertFalse(listener.isPacketStackEmpty());
	}
}
