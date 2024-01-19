package chatsystem.contacts;

import java.net.InetAddress;

/** Represents a contact in the application. **/
public record Contact(String username, InetAddress ip) {
	
	// For two contacts to be considered equal, their usernames are the same.
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Contact) {
			Contact other = (Contact) obj;
			return username.equals(other.username);
		}
		else {
			return false;
		}
    }

	@Override
	public String toString() {
		return username + " (" + ip + ")";
	}
}
