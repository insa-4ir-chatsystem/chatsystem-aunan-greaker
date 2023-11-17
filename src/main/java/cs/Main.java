package cs;

import java.net.SocketException;

public class Main {
	public static void main (String[] args) throws SocketException {
		System.out.println("tester Emilie main class");
		System.out.print(UdpSender.getAllLocalBroadcastAddresses());
	}
}
