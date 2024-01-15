package chatsystem.network.tcp;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

class TCPTests {
	private static final Logger LOGGER = LogManager.getLogger(TCPTests.class);
	@Test
	void SessionTest() throws IOException, InterruptedException {
		LOGGER.info("Running SessionTest...");
		final int LISTENING_PORT = 7777;
		final int WAIT_DELAY = 50;
		ArrayList<String> actual = new ArrayList<>();
		// Starts listening socket with TCPConnection as observer
		TCPListener listener = new TCPListener(LISTENING_PORT);
		listener.addObserver((socket) -> {
            TCPConnection client2;
			try {
				client2 = new TCPConnection(socket);
	            String inputLine;
	            while((inputLine = client2.readMessage()) != null) {
	            	actual.add(inputLine);
	            	LOGGER.trace("Got message: " + inputLine);
	            }
	            LOGGER.trace("Closing client2");
	            client2.closeConnection();
			} catch (IOException e) {
				System.err.println("Failed to get in or outPutStreams");
				e.printStackTrace();
			}
        });
		listener.start();
		Thread.sleep(WAIT_DELAY);
		
		InetAddress ip = InetAddress.getLoopbackAddress();
		TCPConnection client1 = new TCPConnection(ip, LISTENING_PORT);
		
		ArrayList<String> expected = new ArrayList<>();
		client1.sendMessage("Hi");
		expected.add("Hi");
		Thread.sleep(WAIT_DELAY);
		assertEquals(expected, actual);
		
		client1.sendMessage("This is my second message");
		expected.add("This is my second message");
		Thread.sleep(WAIT_DELAY);
		assertEquals(expected, actual);
		client1.closeConnection();
	}
	
	@Test
	void exchangeMessagesTest() throws IOException, InterruptedException {
		LOGGER.info("Running exchangeMessagesTest...");
		final int LISTENING_PORT = 7778;
		final int WAIT_DELAY = 50;
		ArrayList<String> actual = new ArrayList<>();
		// Starts listening socket with TCPConnection as observer
		TCPListener listener = new TCPListener(LISTENING_PORT);
		listener.addObserver((socket) -> {
            TCPConnection client2;
			try {
				client2 = new TCPConnection(socket);
	            String inputLine;
	            while((inputLine = client2.readMessage()) != null) {
	            	if (inputLine.equals("Hi")) {
	            		client2.sendMessage("Hello, nice to meet you!");
	            	}
	            	actual.add(inputLine);
	            	LOGGER.trace("Got message: " + inputLine);
	            }
	            LOGGER.trace("Closing client2");
	            client2.closeConnection();
			} catch (IOException e) {
				System.err.println("Failed to get in or outPutStreams");
				e.printStackTrace();
			}
        });
		listener.start();
		Thread.sleep(WAIT_DELAY);
		
		InetAddress ip = InetAddress.getLoopbackAddress();
		TCPConnection client1 = new TCPConnection(ip, LISTENING_PORT);
		
		ArrayList<String> expected = new ArrayList<>();
		client1.sendMessage("Hi");
		expected.add("Hi");
		Thread.sleep(WAIT_DELAY);
		assertEquals(expected, actual);
		assertEquals("Hello, nice to meet you!", client1.readMessage());
		
		client1.sendMessage("So..");
		expected.add("So..");
		client1.sendMessage("Whats up?");
		expected.add("Whats up?");
		Thread.sleep(WAIT_DELAY);
		assertEquals(expected, actual);
		client1.closeConnection();
	}
	
	@Test
	void twoConnectionsTest() throws IOException, InterruptedException {
		LOGGER.info("Running twoConnectionsTest...");
		final int LISTENING_PORT = 7779;
		final int WAIT_DELAY = 50;
		ArrayList<String> actual = new ArrayList<>();
		// Starts listening socket with TCPConnection as observer
		TCPListener listener = new TCPListener(LISTENING_PORT);
		listener.addObserver((socket) -> {
			Thread handlingThread = new Thread(() -> {
				TCPConnection client2;
				try {
					client2 = new TCPConnection(socket);
		            String inputLine;
		            while((inputLine = client2.readMessage()) != null) {
		            	actual.add(inputLine);
		            	LOGGER.trace("Got message: " + inputLine);
		            }
		            LOGGER.trace("Closing client2");
		            client2.closeConnection();
				} catch (IOException e) {
					System.err.println("Failed to get in or outPutStreams");
					e.printStackTrace();
				}
			});
			handlingThread.start();
        });
		listener.start();
		Thread.sleep(WAIT_DELAY);
		
		InetAddress ip = InetAddress.getLoopbackAddress();
		TCPConnection client1 = new TCPConnection(ip, LISTENING_PORT);
		
		ArrayList<String> expected = new ArrayList<>();
		client1.sendMessage("Hi");
		expected.add("Hi");
		Thread.sleep(WAIT_DELAY);
		assertEquals(expected, actual);
		
		// Adds another client to the mix
		TCPConnection client3 = new TCPConnection(ip, LISTENING_PORT);
		
		client3.sendMessage("Hi Im a new client");
		expected.add("Hi Im a new client");
		Thread.sleep(WAIT_DELAY);
		
		client1.sendMessage("Whats up?");
		expected.add("Whats up?");
		Thread.sleep(WAIT_DELAY);
		
		assertEquals(expected, actual);
		client1.closeConnection();
		client3.closeConnection();
	}
}
