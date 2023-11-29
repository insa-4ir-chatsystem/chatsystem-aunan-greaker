package chatsystem.contacts;

import java.net.InetAddress;

/** Represents a contact in the application. */
public record Contact(String username, InetAddress ip) {
}