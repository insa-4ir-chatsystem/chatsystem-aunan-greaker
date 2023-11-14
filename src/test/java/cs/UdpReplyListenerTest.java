package cs;

import static org.junit.Assert.*;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;


public class UdpReplyListenerTest {
	int timeoutMS = 10;
	
	// Sends UDP packet with msg to loopback on port
	public static void sendLoopbackUDP(String msg, int port) throws SocketException, UnknownHostException {
		DatagramSocket socket = new DatagramSocket(9999);
		DatagramPacket outPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName("localhost"), port);
		try {
			socket.send(outPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.close();
	}
	@Test
	public void zeroReplyTest() {
		Dictionary<String, String> expected = new Hashtable<>();
		UdpReplyListener listener = null;
		try {
			listener = new UdpReplyListener(8888, timeoutMS);
			listener.start();
			//Thread.sleep(timeoutMS);
			//listener.quit();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			fail(e.toString());
		}
		assertEquals(expected, listener.getReplies());
	}
	
	@Test
	public void oneReplyTest() throws UnknownHostException {
		Dictionary<String, String> expected = new Hashtable<>();
		expected.put("Daniel", InetAddress.getByName("localhost").getHostAddress());
		UdpReplyListener listener = null;
		try {
			listener = new UdpReplyListener(8888, timeoutMS);
			listener.start();
			sendLoopbackUDP("Daniel", 8888);
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			fail(e.toString());
		}
		assertEquals(expected, listener.getReplies());
	}
	
	@Test
	public void twoReplyTest() throws UnknownHostException {
		Dictionary<String, String> expected = new Hashtable<>();
		expected.put("Daniel", InetAddress.getByName("localhost").getHostAddress());
		expected.put("Idalia", InetAddress.getByName("localhost").getHostAddress());
		UdpReplyListener listener = null;
		try {
			listener = new UdpReplyListener(8888, timeoutMS);
			listener.start();
			sendLoopbackUDP("Daniel", 8888);
			sendLoopbackUDP("Idalia", 8888);
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			fail(e.toString());
		}
		assertEquals(expected, listener.getReplies());
	}
	
	

}
