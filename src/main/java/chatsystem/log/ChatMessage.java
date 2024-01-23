package chatsystem.log;

import java.net.InetAddress;

/** Represents a chat message in the application. **/
public record ChatMessage(String msg_id, InetAddress from, String msg, String timeStamp) {
}
