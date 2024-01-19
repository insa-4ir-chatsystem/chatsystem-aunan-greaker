package chatsystem.ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.controller.Controller;
import chatsystem.controller.DatabaseController;
import chatsystem.controller.TCPController;
import chatsystem.log.ChatHistory;
import chatsystem.log.ChatMessage;
import chatsystem.network.tcp.TCPConnection;

public class ChatSystemGUI {

	private JFrame frame = new JFrame();
	private JPanel contactsPanel;
	private JPanel chatHistoryPanel;
	private JPanel newChatPanel;
	private JTable contactTable;
	private JTable chatsTable;
	private JButton sendButton = new JButton("Send");
	
	// Private variables to keep track of who the user is chatting with, and the corresponding TCPConnection
    // Disse vurde kanskje være i controlleren? De må vel også være lister siden man kan chatte med flere samtidig
	private static Contact showingChatWith;
	private static final Logger LOGGER = LogManager.getLogger(ChatSystemGUI.class);

	public ChatSystemGUI() {
			initialize();
	}

	public void disableSendButton() {
		sendButton.setEnabled(false);
	}
	
	public void initialize() {
		LOGGER.trace("Initializing ChatSystemGUI...");
		contactsPanel = new JPanel();
		chatHistoryPanel = new JPanel();
		newChatPanel = new JPanel();
		contactTable = new JTable();
		chatsTable = new JTable();
	    newChatPanel.setLayout(new GridBagLayout());
        
        // Set the preferred size of the 'Contacts' table in the GUI and initialize its content
        contactTable.setPreferredScrollableViewportSize(new Dimension(200, 500));
		updateContactTable();
        
        // When selecting contact from the GUI 'Contacts' table, open the corresponding 'Chat' table
        contactTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = contactTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Get the contact username from the selected row, and sends it to the DatabaseController
                        String contactUsername = contactTable.getValueAt(selectedRow, 0).toString();
                        
                        // Update the 'Chat' table to the selected username
                        ContactList contList = ContactList.getInstance(); 
                        Contact selectedContact = contList.getContact(contactUsername);
                		showChatsWith(selectedContact);
                    }
                }
            }
        });    
        
        // Set the preferred size of the 'Chat' table in the GUI, and remove grid lines of table
        chatsTable.setPreferredScrollableViewportSize(new Dimension(880, 500));
        chatsTable.setShowGrid(false);
        chatsTable.setIntercellSpacing(new Dimension(0, 0));
 
        // Create the 'Change Username' button
        JButton changeUserNameButton = new JButton("Change Username");
	    changeUserNameButton.setBorder(BorderFactory.createEmptyBorder(10,50,10,50));
	    newChatPanel.add(changeUserNameButton);	 
        changeUserNameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Creates a window to change username
				ChooseUsernameGUI.initialize();
	        }   
        });
        
        // Create the message field
        JTextField messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(600, 25));
        JLabel lbl = new JLabel("Write your message here: ");
        lbl.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        newChatPanel.add(lbl);
        newChatPanel.add(messageField);

        // Set the border and disable the 'Send' button 
	    sendButton.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
	    sendButton.setEnabled(false);
	    newChatPanel.add(sendButton);

	    // Adding an ActionListener to 'Send' button, to start the sendMsgHandler() of the DatabaseController
	    sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	            String msg = messageField.getText();
	            if (!msg.equals("")) {
					//Starts a connection with the user and sends the message
					TCPController.startChatWith(showingChatWith.ip());
		            TCPController.sendMessage(msg);

					// Store the message in the local chat history
		            DatabaseController.addMsgHandler(showingChatWith, msg);
		            
		            // Remove the message from the messageField after it is sent
		            messageField.setText("");
	            }
	        }
	    });

        // Adding a WindowListener to handle window events
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Code to be executed when the window is closing
                LOGGER.trace("Window is closing. Performing final tasks...");
				Controller.logoutHandler();
            }
        });
        frame.add(contactsPanel, BorderLayout.WEST);
        frame.add(newChatPanel, BorderLayout.SOUTH);
        frame.setTitle("ChatSystem");
        frame.pack();
        frame.setPreferredSize(new Dimension(1200, 600));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Method to close the GUI
	public void close() {
		frame.dispose();
		LOGGER.trace("Closed ChatSystemGUI");
	}
	
	public void updateContactTable() {
		LOGGER.trace("Updating contactTable...");

		// Get the existing table model
		DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Contacts"}, 0);
	
		List<Contact> contactList = ContactList.getInstance().getAllContacts();
	
		// Populate the table model with data from the contactList
		for (int i = 0; i < contactList.size(); i++) {
			String contactName = contactList.get(i).username();
	
			// Add a new row to the table model
			tableModel.addRow(new Object[]{contactName});
		}
	
		// Set the table model for the JTable
		contactTable.setModel(tableModel);
        
        // Make the entire table non-editable
        contactTable.setFocusable(false);
        
        // Add contactTable to the scrollPaneContacts, scrollPaneContacts to the contactsPanel, and contactPanel to the WEST of the frame (and remove any old version of the contactPanel if found)
		contactsPanel.removeAll();
        JScrollPane scrollPaneContacts = new JScrollPane(contactTable);
        contactsPanel.add(scrollPaneContacts);
        frame.remove(contactsPanel);
        frame.add(contactsPanel, BorderLayout.WEST);
        
        // Update the frame
		SwingUtilities.updateComponentTreeUI(frame);
		
    }
	
	public Contact getshowingChatWith() {
		return showingChatWith;
	}

	public void showChatsWith(Contact otherUser) {
		LOGGER.trace("Updating chatsTable for " + otherUser.username());
		// Enable send button
		sendButton.setEnabled(true);
		
		// Update which contact the user is chatting with
		showingChatWith = otherUser;
		
    	// Create a table model with one column for contactNames and no data initially
        DefaultTableModel tableModel = new DefaultTableModel( new Object[]{otherUser.username(), "Me"}, 0);

        // Get the ChatHistory instance with the otherUser
        ChatHistory chatHistory = new ChatHistory(otherUser.ip());
        List<ChatMessage> list = chatHistory.getChatHistory();
        
	    // Populate the table model with data from the chatHistory list
	    for (ChatMessage chatMessage : list) {
	        InetAddress from = chatMessage.from();
	        String msg = chatMessage.msg();
	        
	        // Message size controll, to stop table text overflow in a single table line
	        List<String> msgs = new ArrayList<String>();
	        
	        while (msg.length() > 70) {
	        	for (int i = 70; i >= 0; i--) {
	        		if (Character.toString(msg.charAt(i)).equals(" ")) {
	        			msgs.add(msg.substring(0, i));
	        			msg = msg.substring(i + 1);
	        			msgs.add(msg);
	        			i = -1;
	        		}
	        	}
	        }
	        msgs.add(msg);
	
	        for (String messagePiece : msgs) {
	        	// Add a new row to the table model
		        if (from.equals(otherUser.ip())) {
		            tableModel.addRow(new Object[]{messagePiece, ""});
		        }
		        else {
		            tableModel.addRow(new Object[]{"", messagePiece});
		        }
	        }
	    }      
	
	    // Set the table model for the JTable
	    chatsTable.setModel(tableModel);
	    
	    if (!list.isEmpty()) {
	    	LOGGER.error("ChatHistory list is empty: " + list.isEmpty());
	    	// Push the messages from this user to the right of the table column
		    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
	        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
	        chatsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
	    }
	        
	    // Make the entire table non-editable
	    chatsTable.setEnabled(false);
	    chatsTable.setFocusable(false);
	        
	    // Add chatsTable to the scrollPaneChats, scrollPaneChats to the chatHistoryPanel, and chatHistoryPanel to the CENTER of the frame (and remove any old version of the chatHistoryPanel if found)
	    chatHistoryPanel.removeAll();
	    JScrollPane scrollPaneChats = new JScrollPane(chatsTable);
	    chatHistoryPanel.add(scrollPaneChats);
	    frame.remove(chatHistoryPanel);
	    frame.add(chatHistoryPanel, BorderLayout.CENTER);
	        
	    // Update the frame
	    SwingUtilities.updateComponentTreeUI(frame);
    }
}
