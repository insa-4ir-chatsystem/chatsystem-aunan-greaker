package chatsystem.network.udp;

import java.net.InetAddress;

public record UDPMessage(String text, InetAddress source) {

}
