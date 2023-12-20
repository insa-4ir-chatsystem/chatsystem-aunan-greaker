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
import chatsystem.contacts.Contact;

public class ChooseUsernameGUI {
	//public static String myUsername;
	//private JTextField usernameField;
	
	private boolean UsernameIsAvailable(String username) throws UnknownHostException {
		ContactList contactList = ContactList.getInstance();
		Contact newContact = new Contact(username, InetAddress.getLocalHost());
        return !contactList.hasContact(newContact);
	}
	
	public ChooseUsernameGUI() {
		final JFrame frame = new JFrame();
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(0, 1));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    JLabel usernameLabel = new JLabel("Username: ");
	    JTextField usernameField = new JTextField(20);

	    JButton loginButton = new JButton("Login");

	    loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String myUsername = usernameField.getText();
	            try {
					if (UsernameIsAvailable(myUsername) && myUsername != "") {
						frame.dispose();
						new ChatSystemGUI(myUsername);
					} else {
					    JOptionPane.showMessageDialog(frame, "This username is not available, please choose a different one");	            	            
					}
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
}