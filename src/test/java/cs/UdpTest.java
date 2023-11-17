package cs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.junit.Test;

public class UdpTest {

	@Test
	public void UdpSenderTest() {
		UdpSender sender = new UdpSender(8888, 9999);
		try {
			sender.send("this is a test".getBytes(), InetAddress.getLocalHost());
		} catch (IOException e) {
			fail("Failed with IOException: " + e);
		}
	}
	
	@Test
	public void getAllLocalBroadcastAddressesTest() {
		try {
			UdpSender.getAllLocalBroadcastAddresses();
		} catch (SocketException e) {
			fail("Failed with SocketException: " + e);
		}
	}

	@Test
	public void UdpListenerTest() throws SocketException, InterruptedException {
		UdpListener listener = new UdpListener(8888, 100);
		listener.start();
		try {
			UdpSender sender = new UdpSender(8888, 9999);
			sender.send("TestPacket".getBytes(), InetAddress.getLocalHost());
		} catch (IOException e) {
			fail("Failed because of UdpSender. " + e);
		}
		Thread.sleep(100);
		assertFalse(listener.isPacketStackEmpty());
	}
}