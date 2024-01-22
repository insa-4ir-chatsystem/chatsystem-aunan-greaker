package chatsystem.ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
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
import chatsystem.contacts.ContactAlreadyExists;
import chatsystem.contacts.ContactList;
import chatsystem.controller.Controller;
import chatsystem.controller.DatabaseController;
import chatsystem.controller.TCPController;
import chatsystem.log.ChatHistory;
import chatsystem.log.ChatMessage;

public class ChatSystemGUI {

	private JFrame frame = new JFrame();
	private JPanel contactsPanel;
	private JPanel chatHistoryPanel;
	private JPanel closeChatPanel;
	private JPanel newChatPanel;
	private JTable contactTable;
	private JTable chatsTable;
	private JButton closeChatButton = new JButton("X");
	private JButton sendButton = new JButton("Send");
	
	// Private variables to keep track of who the user is chatting with, and the corresponding TCPConnection
	private static Contact showingChatWith;
	private static final Logger LOGGER = LogManager.getLogger(ChatSystemGUI.class);

	public ChatSystemGUI() {
		initialize();
	}

	public void disableSendButton() {
		sendButton.setEnabled(false);
	}
	
	/**	Method to initialize the GUI*/
	public void initialize() {
		LOGGER.trace("Initializing ChatSystemGUI...");
		contactsPanel = new JPanel();
		chatHistoryPanel = new JPanel();
		closeChatPanel = new JPanel();
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
                        
                		if (contactUsername.contains(" - New Messages (")) {
                			// Remove new messages notification
                			int lastNewMessages = contactUsername.lastIndexOf(" - New Messages (");
                			String newUsername = contactUsername.substring(0, lastNewMessages);
                			ContactList.getInstance().removeContact(selectedContact);
                			selectedContact = new Contact(newUsername, selectedContact.ip());
                			try {
								ContactList.getInstance().addContact(selectedContact);
							} catch (ContactAlreadyExists e1) {
								LOGGER.error("Contact Already Exists error when clicking contact in contactTable containing new messages notification: " + e1);
							}
                			updateContactTable();
                		}
                		
                		showChatsWith(selectedContact);
                    }
                }
            }
        });    
        
        // Set the preferred size of the 'Chat' table in the GUI, and remove grid lines of table
        chatsTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
        chatsTable.setShowGrid(false);
        chatsTable.setIntercellSpacing(new Dimension(0, 0));
        
        // Set the ActionListener for the closeChatButton and add the closeChatButton to the closeChatPanel
        closeChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showingChatWith = null;
                frame.remove(chatHistoryPanel);
                frame.remove(closeChatPanel);
                
                // Update the frame
                SwingUtilities.updateComponentTreeUI(frame);
                
                // Remove selection focus when closing chat
                contactTable.getSelectionModel().clearSelection();
	        }   
        });
 
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
					// Sends the message to the other user and stores it in the local chat history
		            try {
						TCPController.sendMessageHandler(showingChatWith.ip(), msg);
					} catch (IOException e2) {
						LOGGER.error("Could not start TCPConnection with " + showingChatWith.ip() + " because: " + e2.getMessage());
						return;
					}

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
        frame.setTitle("ChatSystem - " + Controller.getMyUsername());
        frame.pack();
        frame.setMinimumSize(new Dimension(1300, 600));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**	Method to close the GUI*/
	public void close() {
		frame.dispose();
		LOGGER.trace("Closed ChatSystemGUI");
	}
	
	/**	Method to update the 'Contacts' table in the GUI*/
	public void updateContactTable() {
		LOGGER.debug("Updating contactTable...");

		// Get the existing table model
		DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Contacts"}, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	
		List<Contact> contactList = ContactList.getInstance().getAllContacts();
	
		// Populate the table model with data from the contactList
		for (int i = contactList.size() -1; i >= 0; i--) {
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
		SwingUtilities.updateComponentTreeUI(contactsPanel);
		
    }
	
	public Contact getshowingChatWith() {
		return showingChatWith;
	}

	/**	Method to update the 'Chat' table in the GUI*/
	public void showChatsWith(Contact otherUser) {
		LOGGER.trace("Updating chatsTable for " + otherUser.username());
		// Enable send button
		sendButton.setEnabled(true);
		
		// Update which contact the user is chatting with
		showingChatWith = otherUser;
		
    	// Create a table model with one column for contactNames and no data initially
        DefaultTableModel tableModel = new DefaultTableModel( new Object[]{otherUser.username(), Controller.getMyUsername(), "Sent"}, 0);

        // Get the ChatHistory instance with the otherUser
        ChatHistory chatHistory = new ChatHistory(otherUser.ip());
        List<ChatMessage> list = chatHistory.getChatHistory();
        
	    // Populate the table model with data from the chatHistory list
	    for (ChatMessage chatMessage : list) {
	        InetAddress from = chatMessage.from();
	        String msg = chatMessage.msg();
	        String time = chatMessage.timeStamp();
	        
	        // Message size controll, to stop table text overflow in a single table line
	        List<String> msgs = new ArrayList<String>();
	        
		    while (msg.length() > 50) {
		        for (int i = 50; i >= 0; i--) {
		        	if (Character.toString(msg.charAt(i)).equals(" ")) {
		        		msgs.add(msg.substring(0, i));
		        		msg = msg.substring(i + 1);
		        		i = -1;
		        	}
		        }
		    }
	        msgs.add(msg);
	
	        // Timestamp is only printed at the first message
	        Boolean firstMsg = true;
	        for (String messagePiece : msgs) {
	        	// Add a new row to the table model
		        if (from.equals(otherUser.ip())) {
		        	if(firstMsg) {
		        		tableModel.addRow(new Object[]{messagePiece, "", time});
		        	}
		        	else {
		        		tableModel.addRow(new Object[]{messagePiece, "", ""});
		        	}
		        }
		        else {
		        	if(firstMsg) {
		        		tableModel.addRow(new Object[]{"", messagePiece, time});
		        	}
		        	else {
		        		tableModel.addRow(new Object[]{"", messagePiece, ""});
		        	}
		        }
		        firstMsg = false;
	        }
	    }      
	
	    // Set the table model for the JTable, and set the size of the table columns
	    chatsTable.setModel(tableModel);
	    chatsTable.getColumnModel().getColumn(0).setMinWidth(400);
	    chatsTable.getColumnModel().getColumn(1).setMinWidth(400);
	    chatsTable.getColumnModel().getColumn(2).setMinWidth(200);
	    chatsTable.getColumnModel().getColumn(0).setMaxWidth(400);
	    chatsTable.getColumnModel().getColumn(1).setMaxWidth(400);
	    chatsTable.getColumnModel().getColumn(2).setMaxWidth(200);
	    
	    // Push the messages from this user to the right of the table column
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
	    rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        chatsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        chatsTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
	        
	    // Make the entire table non-editable
	    chatsTable.setEnabled(false);
	    chatsTable.setFocusable(false);
	        
	    // Add chatsTable to the scrollPaneChats, scrollPaneChats to the chatHistoryPanel, and chatHistoryPanel to the CENTER of the frame (and remove any old version of the chatHistoryPanel if found)
	    LOGGER.trace("Adding contactTable to the scrollPaneContacts...");
		chatHistoryPanel.removeAll();
	    JScrollPane scrollPaneChats = new JScrollPane(chatsTable);
	    chatHistoryPanel.add(scrollPaneChats);
	    frame.remove(chatHistoryPanel);
	    frame.add(chatHistoryPanel, BorderLayout.CENTER);
	    
	    // Add the closeChatPanel to the frame
	    closeChatPanel = new JPanel();
	    closeChatPanel.add(closeChatButton);
	    frame.remove(closeChatPanel);
	    frame.add(closeChatPanel, BorderLayout.EAST);
	        
	    // Update the frame
		LOGGER.trace("Running updateComponentTreeUI()...");
		SwingUtilities.updateComponentTreeUI(chatHistoryPanel);
    }
	
	/**	Method to indicate that a new unread message has been received*/
	public void newUnreadMessage(Contact fromContact) {
		LOGGER.trace("New unread message indicated in contactTable...");

		// Remove fromContact from the contactList to make space for the new messages version of this contact
		ContactList.getInstance().removeContact(fromContact);
		Contact contactNewMessages = new Contact("Naming Error", fromContact.ip());
		
		if (fromContact.username().contains(" - New Messages (")) {
			// Get the previous number of unread messages with this user
			int lastLeftParantheses = fromContact.username().lastIndexOf("(");
			int lastRightParantheses = fromContact.username().lastIndexOf(")");
			int msgs = Integer.valueOf(fromContact.username().substring(lastLeftParantheses + 1, lastRightParantheses));
			msgs++;
			LOGGER.trace("New unread messages number: " + msgs + ", lastLeftParantheses: " + lastLeftParantheses + ", lastRightParantheses: " + lastRightParantheses);
			
			// Set the new username, indicating the new number of unread messages
			String newUsername = fromContact.username().substring(0, lastLeftParantheses + 1) + msgs + ")";
			contactNewMessages = new Contact(newUsername, fromContact.ip());
		}
		else {
			// If no previous new messages, set new message counter to 1
			contactNewMessages = new Contact(fromContact.username() + " - New Messages (1)", fromContact.ip());
		}
		
		// Add newly modified contact indicating new messages
		try {
			ContactList.getInstance().addContact(contactNewMessages);
		} catch (ContactAlreadyExists e) {
			LOGGER.error("Contact Already Exists resulting in not adding new messages edit of contact to the contactlist: " + e);
		}
		
		// Call to update the contactTable with the "new" contact
		updateContactTable();
	}
	
	/**	Method to indicate that a contact has changed username*/
	public void changedUsername(String oldUsername, String newUsername) {
		if (showingChatWith != null && showingChatWith.username().equals(oldUsername)) {
			showingChatWith = new Contact(newUsername, showingChatWith.ip());
			showChatsWith(showingChatWith);
		}
		JOptionPane.showMessageDialog(frame, oldUsername + " changed username to " + newUsername);
	}
	
	public void setFrameTitle(String username) {
		frame.setTitle("ChatSystem - " + username);
		
		// Update the frame
		SwingUtilities.updateComponentTreeUI(frame);
		SwingUtilities.updateComponentTreeUI(closeChatButton);
	}
}
