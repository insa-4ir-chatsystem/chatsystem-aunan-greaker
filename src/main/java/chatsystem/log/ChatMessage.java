package chatsystem.log;

import java.net.InetAddress;
import java.time.LocalDateTime;

/*	Class that represents a single chat message sent in the chat system*/
public record ChatMessage(String text, InetAddress src, InetAddress dst, LocalDateTime dateTime) {

}
