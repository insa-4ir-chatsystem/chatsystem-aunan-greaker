package chatsystem.contacts;

/* Error that is thrown when a contact is added twice to the list. */
@SuppressWarnings("serial")
public class ContactAlreadyExists extends Exception {

    private final Contact contact;

    public ContactAlreadyExists(Contact contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "ContactAlreadyExists{" + contact + "}";
    }
}