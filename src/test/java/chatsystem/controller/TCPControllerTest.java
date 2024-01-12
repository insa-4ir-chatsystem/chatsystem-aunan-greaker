package chatsystem.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;

import org.junit.jupiter.api.Test;

class TCPControllerTest {
	private final int SLEEP_DELAY = 50;
	@Test
	void establishChatTest() throws InterruptedException {
		TCPController.startTCPListener();
		Thread.sleep(SLEEP_DELAY);
		TCPController.startChatWith(InetAddress.getLoopbackAddress());
	}
	//TODO Write test for sending messages in chat

}
