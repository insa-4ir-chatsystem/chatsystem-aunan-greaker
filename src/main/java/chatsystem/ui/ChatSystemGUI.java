package chatsystem.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.controller.DatabaseController;
import chatsystem.controller.TCPController;
import chatsystem.controller.UDPController;
import chatsystem.log.Database;
import chatsystem.log.TableAlreadyExists;
import chatsystem.network.tcp.TCPConnection;
import chatsystem.network.udp.UDPSender;

public class ChatSystemGUI {

	private JFrame frame = new JFrame();
	private JPanel contactsPanel;
	private JPanel chatHistoryPanel;
	private JPanel newChatPanel;
	private JTable contactTable;
	private JTable chatsTable;
	private JButton sendButton;
	
	// Private variables to keep track of who the user is chatting with, and the corresponding TCPConnection
    // Disse vurde kanskje være i controlleren? De må vel også være lister siden man kan chatte med flere samtidig
	private static Contact chattingWith;
	private static TCPConnection connection;
	private static final Logger LOGGER = LogManager.getLogger(ChatSystemGUI.class);

	public ChatSystemGUI() {
		SwingUtilities.invokeLater(() -> {
			initialize();
		});
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
                		updateChatsTable(selectedContact);
                    }
                }
            }
        });    
        
        // Set the preferred size of the 'Chat' table in the GUI
        chatsTable.setPreferredScrollableViewportSize(new Dimension(500, 500));
 
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

        // Create the 'Send' button to send 
	    JButton sendButton = new JButton("Send");
	    sendButton.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
	    sendButton.setEnabled(false);
	    newChatPanel.add(sendButton);

	    // Adding an ActionListener to 'Send' button, to start the sendMsgHandler() of the DatabaseController
	    sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	            String msg = messageField.getText();
	            if (!msg.equals("")) {
		            connection.sendMessage(msg);
		            DatabaseController.sendMsgHandler(chattingWith, msg);
	            }
	        }
	    });

        // Adding a WindowListener to handle window events
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Code to be executed when the window is closing
                LOGGER.trace("Window is closing. Performing final tasks...");
				UDPController.logoutHandler();
            }
        });
        frame.add(contactsPanel, BorderLayout.WEST);
        frame.add(newChatPanel, BorderLayout.SOUTH);
        frame.setTitle("ChatSystem");
        frame.pack();
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
        //contactTable.setEnabled(false);
        
        // Add contactTable to the scrollPaneContacts, scrollPaneContacts to the contactsPanel, and contactPanel to the WEST of the frame (and remove any old version of the contactPanel if found)
		contactsPanel.removeAll();
        JScrollPane scrollPaneContacts = new JScrollPane(contactTable);
        contactsPanel.add(scrollPaneContacts);
        frame.remove(contactsPanel);
        frame.add(contactsPanel, BorderLayout.WEST);
        
        // Update the frame
		LOGGER.debug("Updating frame...");
        if (frame != null) {
			SwingUtilities.updateComponentTreeUI(frame);
		} else {
			LOGGER.error("frame is null. Make sure it is properly initialized.");
		}
		
    }
	
	public void updateChatsTable(Contact otherUser) {
		// Enable send button
		sendButton.setEnabled(true);
		
		// Update which contact the user is chatting with
		chattingWith = otherUser;
		connection = TCPController.startChatWith(otherUser.ip());
		
    	// Create a table model with one column for contactNames and no data initially
        DefaultTableModel tableModel = new DefaultTableModel( new Object[]{otherUser.username(), "Me"}, 0);
        Database db = Database.getInstance();
        
        // If there is no saved chatHistory linked to this IP, create a new chat table in the database
        try {
			if (!db.hasTable(otherUser.ip().toString())) {
				db.newTable(otherUser.ip().toString());
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		} catch (TableAlreadyExists e) {
			LOGGER.error(e.getMessage());
		}
        
        try {
	        ResultSet rs = db.getTable(otherUser.ip().toString());
	        
	        // Populate the table model with data from the chatHistory
	        while (rs.next()) {
	            String from = rs.getString("from_contact");
	            String msg = rs.getString("msg");
	
	            // Add a new row to the table model
	            if (from.equals(otherUser.ip().toString())) {
	            	tableModel.addRow(new Object[]{msg, ""});
	            }
	            else {
	            	tableModel.addRow(new Object[]{"", msg});
	            }
	        }
	        
        } catch (SQLException e) {
        	LOGGER.error(e.getMessage());
        }
	
	    // Set the table model for the JTable
	    chatsTable.setModel(tableModel);
	        
	    // Make the entire table non-editable
	    chatsTable.setEnabled(false);
	        
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
