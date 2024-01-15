package chatsystem.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.jupiter.api.Test;

import chatsystem.network.tcp.TCPConnection;

class TCPControllerTest {
	private final int SLEEP_DELAY = 50;
	@Test
	void establishChatTest() throws InterruptedException, IOException {
		TCPController.startTCPListener();
		Thread.sleep(SLEEP_DELAY);
		TCPConnection chatconnection = TCPController.startChatWith(InetAddress.getLoopbackAddress());
	}


}
