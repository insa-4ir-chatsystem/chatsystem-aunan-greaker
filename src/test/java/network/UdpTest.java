package network;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import org.junit.Test;

import chatsystem.network.UdpListener;
import chatsystem.network.UdpSender;


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
	public void sendBroadcastTest() throws IOException, InterruptedException {
		String expected = "BroadcastTestMsg";
		UdpListener listener = new UdpListener(8888, 200);
		listener.start();
		UdpSender sender = new UdpSender(8888, 9876);
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
