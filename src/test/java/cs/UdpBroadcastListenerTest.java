package cs;

import static org.junit.Assert.*;

import java.net.SocketException;

import org.junit.Test;

public class UdpBroadcastListenerTest {

	@Test
	public void test() throws SocketException {
		UdpBroadcastSender sender = new UdpBroadcastSender("This is a broadcast", 8888);
		UdpBroadcastListener listener = new UdpBroadcastListener("Emilie", 8888);
		
		listener.start();
		sender.start();
	}

}
