package cs;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChooseUsernameGUI {
	private JTextField usernameField;
	
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
	            String username = usernameField.getText();
	            ArrayList<String> contactList = new ArrayList<String> ();
	            /*contactList = new ContactList();
	            boolean usernameTaken = contactList.getAllNames.contains(username);
	            if (!usernameTaken && username != "") {
	            	frame.dispose();
	            	new ChatSystemGUI(username);
	            } else {
	                JOptionPane.showMessageDialog(frame, "This username is already taken, please choose another one");	            	            
	            }
	            */
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
