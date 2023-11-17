package cs;

import java.net.SocketException;

public class Main {
	public static void main (String[] args) throws SocketException {
		System.out.println("tester Emilie main class");
		new UdpBroadcastListener("Emilie", 8888).start();
		new UdpBroadcastSender("This is a broadcast", 8888).start();
	}
}
