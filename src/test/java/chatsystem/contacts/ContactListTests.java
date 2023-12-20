package chatsystem.contacts;

import static org.junit.jupiter.api.Assertions.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContactListTests {

	/* Will clear contact list at the start of every new test */
	@BeforeEach
	public void clearContactList() {
		ContactList.getInstance().clear();
	}

	@Test
	void contactAdditionTest() throws ContactAlreadyExists, UnknownHostException {
		ContactList contacts = ContactList.getInstance();
		Contact contact1 = new Contact("Idalia", InetAddress.getLocalHost());
		Contact contact2 = new Contact("Katti", InetAddress.getLocalHost());

		assert !contacts.hasContact(contact1);
		contacts.addContact(contact1);
		assert contacts.hasContact(contact1);
		assert !contacts.hasContact(contact2);


		assert !contacts.hasContact(contact2);
		contacts.addContact(contact2);
		assert contacts.hasContact(contact2);
		assert contacts.hasContact(contact1);
	}

	@Test
	void contactDuplicationTest() throws ContactAlreadyExists, UnknownHostException {
		ContactList contacts = ContactList.getInstance();
		Contact contact1 = new Contact("Idalia", InetAddress.getLocalHost());

		contacts.addContact(contact1);
		assert contacts.hasContact(contact1);

		try {
			contacts.addContact(contact1);
			fail("Expected ContactAlreadyExists exception");
		} catch (ContactAlreadyExists e) {
			// expected outcome
		}
	}
}