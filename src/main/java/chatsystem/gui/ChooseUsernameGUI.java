package chatsystem.gui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import chatsystem.network.ContactList;

public class ChooseUsernameGUI {
	public static String myUsername;
	private JTextField usernameField;
	
	public static boolean AvailableUsername(String username) {
		ContactList contactList = new ContactList(username);
        return contactList.getAllNames().contains(username);
	}
	
	public ChooseUsernameGUI() {
		final JFrame frame = new JFrame();
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(0, 1));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(20);

	    JButton loginButton = new JButton("Login");

	    loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	myUsername = usernameField.getText();
	            if (!AvailableUsername(myUsername) && myUsername != "") {
	            	frame.dispose();
	            	new ChatSystemGUI(myUsername);
	            } else {
	                JOptionPane.showMessageDialog(frame, "This username is already taken, please choose another one");	            	            
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
