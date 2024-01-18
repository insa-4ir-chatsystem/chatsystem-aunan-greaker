package chatsystem.ui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.ContactList;
import chatsystem.controller.UDPController;
import chatsystem.contacts.Contact;

public class ChooseUsernameGUI {
	private static final Logger LOGGER = LogManager.getLogger(ChooseUsernameGUI.class);
	private static JTextField usernameField = new JTextField(20);
	
	public static boolean UsernameIsAvailable(String username) throws UnknownHostException {
		ContactList contactList = ContactList.getInstance();
		Contact newContact = new Contact(username, InetAddress.getLocalHost());
        return !contactList.hasContact(newContact);
	}
	
	public static void initialize() {
		final JFrame frame = new JFrame();
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(0, 1));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    JLabel usernameLabel = new JLabel("Username: ");

	    JButton loginButton = new JButton("Login");

	    loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				LOGGER.trace("Login button pressed");
            	String myUsername = usernameField.getText();
				// If we are already online, we must logout before checking if a username is available or logging in again
				if (UDPController.isOnline) {
						UDPController.logoutHandler();
				}

				// Check if the username is available
				if (UDPController.usernameAvailableHandler(myUsername) && !myUsername.equals("")) {
					LOGGER.debug("Username '"+ myUsername + "' was available, logging in...");
					frame.dispose();
					UDPController.loginHandler(myUsername);
				} else {
					JOptionPane.showMessageDialog(frame, "This username is not available, please choose a different one");	            	            
				}
	        }
	    });
	    
	    panel.add(usernameLabel);
	    panel.add(usernameField);
        panel.add(loginButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("Choose Username for ChatSystem");
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		LOGGER.trace("Initialized ChooseUsernameGUI");
	}
	
	public static String getUsername() {
		return usernameField.getText();
	}
}