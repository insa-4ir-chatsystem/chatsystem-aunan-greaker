package chatsystem.network;

import java.net.InetAddress;

public record UDPMessage(String text, InetAddress source) {

}
