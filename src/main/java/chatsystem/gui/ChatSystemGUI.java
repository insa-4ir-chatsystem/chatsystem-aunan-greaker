package chatsystem.gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ChatSystemGUI {
	private JTextField messageField;
	//private GroupLayout group; Kommenterte ut fordi eclipse ga warning
	
	public ChatSystemGUI(String username) {
		final JFrame frame = new JFrame();
	    JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout(1, 2));

	    JLabel messageLabel = new JLabel("Write your message here: ");
        messageField = new JTextField(20);

	    JButton sendButton = new JButton("Send");

	    sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	            //String message = messageField.getText(); Kommenterte ut fordi eclipse ga warning
	        }
	    });
	    
	    panel.add(messageLabel);
	    panel.add(messageField);
        panel.add(sendButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("ChatSystem");
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
