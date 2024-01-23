package chatsystem.ui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.controller.Controller;
import chatsystem.controller.UDPController;

public class ChooseUsernameGUI {
	private static final Logger LOGGER = LogManager.getLogger(ChooseUsernameGUI.class);
	private static JTextField usernameField = new JTextField(20);
	
	/**	Initializes the ChooseUsernameGUI */
	public static void initialize() {
		LOGGER.trace("Initializing ChooseUsernameGUI...");
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

				// Check if the username is available
				if (!UDPController.usernameAvailableHandler(myUsername) && !myUsername.equals("")) {
					JOptionPane.showMessageDialog(frame, "This username is not available, please choose a different one");
					return;
				}
				// If we are not online, login
				if (!Controller.isOnline() ) {
					LOGGER.debug("Username '"+ myUsername + "' was available, logging in...");
					frame.dispose();
					Controller.loginHandler(myUsername);
				}
				// If we are already online, set new username
				else {
					Controller.changeUsernameHandler(myUsername);
					frame.dispose();
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

		// If we are not online, close the application when the window is closed
		if (!Controller.isOnline()) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}
	
	public static String getUsername() {
		return usernameField.getText();
	}
}