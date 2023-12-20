package chatsystem.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ChatSystemGUI {
	private JTextField messageField;
	
	public ChatSystemGUI(String username) {
		final JFrame frame = new JFrame();
	    JPanel newMessagePanel = new JPanel();
	    newMessagePanel.setLayout(new GridLayout(0, 2));

	    //JLabel messageLabel = new JLabel("Write your message here: ");
        messageField = new JTextField(200);
        //messageField.setBorder(BorderFactory.createLineBorder(Color.decode("#2C6791")));

	    JButton sendButton = new JButton("Send");

	    sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	            //String message = messageField.getText(); Kommenterte ut fordi eclipse ga warning
	        }
	    });
	    
	    //panel.add(messageLabel);
	    newMessagePanel.add(messageField);
	    newMessagePanel.add(sendButton);

        frame.add(newMessagePanel, BorderLayout.CENTER);
        frame.setTitle("ChatSystem");
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
