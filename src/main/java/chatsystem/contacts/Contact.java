package chatsystem.contacts;

import java.net.InetAddress;

/** Represents a contact in the application. **/
public record Contact(String username, InetAddress ip) {
	
	// For two contacts to be considered equal, their usernames are equal to one another
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

	// Contact.toString() should have this format: username (ip)
	@Override
	public String toString() {
		return username + " (" + ip + ")";
	}
}
