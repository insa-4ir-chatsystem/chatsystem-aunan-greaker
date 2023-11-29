package network;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import org.junit.Test;

import chatsystem.network.UDPListener;
import chatsystem.network.UDPSender;


public class UdpTest {

	@Test
	public void UdpSenderTest() {
		UDPSender sender = new UDPSender(8888, 9999);
		try {
			sender.send("this is a test".getBytes(), InetAddress.getLocalHost());
		} catch (IOException e) {
			fail("Failed with IOException: " + e);
		}
	}
	
	@Test
	public void sendBroadcastTest() throws IOException, InterruptedException {
		String expected = "BroadcastTestMsg";
		UDPListener listener = new UDPListener(8888, 200);
		listener.start();
		UDPSender sender = new UDPSender(8888, 9876);
		sender.sendBroadcast("BroadcastTestMsg".getBytes());
		Thread.sleep(200);
		try {
			assertEquals(expected, (new String(listener.peekPacketStack().getData(), 0, listener.peekPacketStack().getLength())));
		} catch (EmptyStackException e) {
			fail("No Msg received");
		}
	}
	
	@Test
	public void getAllLocalBroadcastAddressesTest() {
		try {
			UDPSender.getAllLocalBroadcastAddresses();
		} catch (SocketException e) {
			fail("Failed with SocketException: " + e);
		}
	}

	@Test
	public void UdpListenerTest() throws SocketException, InterruptedException {
		UDPListener listener = new UDPListener(8888, 100);
		listener.start();
		try {
			UDPSender sender = new UDPSender(8888, 9999);
			sender.send("TestPacket".getBytes(), InetAddress.getLocalHost());
		} catch (IOException e) {
			fail("Failed because of UdpSender. " + e);
		}
		Thread.sleep(100);
		assertFalse(listener.isPacketStackEmpty());
	}
}
