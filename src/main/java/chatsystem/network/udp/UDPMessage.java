package chatsystem.network.udp;

import java.net.InetAddress;

/** Class representing a UDP message*/
public record UDPMessage(String text, InetAddress source) {

}
