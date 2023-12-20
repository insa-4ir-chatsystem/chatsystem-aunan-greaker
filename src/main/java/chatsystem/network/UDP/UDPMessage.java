package chatsystem.network.UDP;

import java.net.InetAddress;

public record UDPMessage(String text, InetAddress source) {

}
