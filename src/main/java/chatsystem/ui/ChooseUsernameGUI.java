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

import chatsystem.contacts.ContactList;
import chatsystem.controller.UDPController;
import chatsystem.contacts.Contact;

public class ChooseUsernameGUI {
	private static JTextField usernameField = new JTextField(20);;
	
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
            	String myUsername = usernameField.getText();
				if (UDPController.usernameAvailableHandler(myUsername) && !myUsername.equals("")) {
					frame.dispose();
					new ChatSystemGUI(myUsername);
					UDPController.loginHandler();
				} else {
					JOptionPane.showMessageDialog(frame, "This username is not available, please choose a different one");	            	            
				}
	        }
	    });
	    
	    panel.add(usernameLabel);
	    panel.add(usernameField);
        panel.add(loginButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("ChatSystem Login");
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static String getUsername() {
		return usernameField.getText();
	}
}