package chatsystem.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chatsystem.network.tcp.TCPConnection;

class TCPControllerTest {
	private static final int SLEEP_DELAY = 50;
	
	@BeforeAll
	/** Starts the TCPListener */
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
	void establishChatTest() throws InterruptedException, IOException {
		TCPConnection chatconnection = TCPController.startChatWith(InetAddress.getLoopbackAddress());
		chatconnection.closeConnection();
	}

	@Test
	void handleIncomingTCPConnectionTest() throws InterruptedException, IOException {
		TCPConnection chatconnection = TCPController.startChatWith(InetAddress.getLoopbackAddress());
		chatconnection.sendMessage("Hello");
		chatconnection.sendMessage("How are you?");
		chatconnection.sendMessage("Goodbye");
		chatconnection.closeConnection();
	}

	@Test /** Test with 3 clients connecting and sending messages at different times */
	void multipleChatsTest() throws InterruptedException, IOException {
		TCPConnection chatconnection = TCPController.startChatWith(InetAddress.getLoopbackAddress());
		chatconnection.sendMessage("Hello Im connection 1");
		
		TCPConnection chatconnection2 = TCPController.startChatWith(InetAddress.getLoopbackAddress());
		chatconnection.sendMessage("How are you?");

		chatconnection2.sendMessage("Hello Im connection 2");
		chatconnection.sendMessage("Goodbye");
		TCPConnection chatconnection3 = TCPController.startChatWith(InetAddress.getLoopbackAddress());
		chatconnection3.sendMessage("Hello Im connection 3. Bye!");
		chatconnection3.closeConnection();
		chatconnection.closeConnection();
		chatconnection2.sendMessage("How are you? Bye!");
		chatconnection2.closeConnection();
	}


}
