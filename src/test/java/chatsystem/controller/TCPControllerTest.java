package chatsystem.controller;


import java.io.IOException;
import java.net.InetAddress;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TCPControllerTest {
	private static final int SLEEP_DELAY = 50;
	
	@BeforeAll
	/** Starts the TCPListener and gives it time to start*/
	static void startListener() throws InterruptedException {
		TCPController.startTCPListener();
		Thread.sleep(SLEEP_DELAY);
	}
	@AfterAll
	/** Stops the TCPListener */
	static void stopListener() throws InterruptedException {
		TCPController.stopTCPListener();
	}

	@Test
	void sendMessageTest() throws InterruptedException, IOException {
		TCPController.sendMessageHandler(InetAddress.getLoopbackAddress() ,"Hello");
		TCPController.sendMessageHandler(InetAddress.getLoopbackAddress(), "How are you?");
		TCPController.sendMessageHandler(InetAddress.getLoopbackAddress(), "Goodbye");
	}


}
