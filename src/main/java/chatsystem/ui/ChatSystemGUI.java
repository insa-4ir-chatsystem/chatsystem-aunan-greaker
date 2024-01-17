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
import chatsystem.controller.UDPController;
import chatsystem.log.Database;
import chatsystem.log.TableAlreadyExists;
import chatsystem.network.udp.UDPSender;

public class ChatSystemGUI {
	private static final JFrame frame = new JFrame();
	private static JPanel contactsPanel;
	private static JPanel chatHistoryPanel;
	private static JPanel newChatPanel;
	private static JTable contactTable;
	private static JTable chatsTable;
	private static JButton sendButton;
	
	private static final Logger LOGGER = LogManager.getLogger(ChatSystemGUI.class);
	
	public ChatSystemGUI(String username) {
		contactsPanel = new JPanel();
		chatHistoryPanel = new JPanel();
		newChatPanel = new JPanel();
		contactTable = new JTable();
		chatsTable = new JTable();
	    newChatPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        contactTable.setPreferredScrollableViewportSize(new Dimension(200, 500));
        updateContactTable();
        
        // Set functionality for selecting contact from the GUI 'Contacts' table
        /*contactTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = requestedHelpTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Get the data from the selected row
                        String selectedId = requestedHelpTable.getValueAt(selectedRow, 0).toString();
                        String selectedUsername = requestedHelpTable.getValueAt(selectedRow, 1).toString();
                        String selectedText = requestedHelpTable.getValueAt(selectedRow, 2).toString();

                        // Open a new interface with the selected data
                        openDetailsInterface(selectedId, selectedUsername, selectedText, username);
                    }
                }
            }
        });
        */
        
        chatsTable.setPreferredScrollableViewportSize(new Dimension(500, 500));
        JScrollPane scrollPaneChats = new JScrollPane(chatsTable);
        chatHistoryPanel.add(scrollPaneChats);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        newChatPanel.add(new JLabel("Write your message here: "));
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        JTextField messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(600, 25));
        newChatPanel.add(messageField);

        gbc.gridx = 0;
        gbc.gridy = 3;
	    JButton sendButton = new JButton("Send");
	    sendButton.setEnabled(false);
	    newChatPanel.add(sendButton);

	    sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	            String msg = messageField.getText();
	            // TODO - msg receiver;
	            DatabaseController.sendMsgHandler(msg);
	        }
	    });

        // Adding a WindowListener to handle window events
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Code to be executed when the window is closing
                LOGGER.trace("Window is closing. Performing cleanup or final tasks...");
				UDPController.onExit();
            }
        });
        frame.add(contactsPanel, BorderLayout.WEST);
        frame.add(newChatPanel, BorderLayout.SOUTH);
        frame.setTitle("ChatSystem");
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void updateContactTable() {
    	// Create a table model with one column for contactNames and no data initially
        DefaultTableModel tableModel = new DefaultTableModel( new Object[]{"Contacts"}, 0);

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
        contactTable.setEnabled(false);
        
        // Add contactTable to the scrollPaneContacts, scrollPaneContacts to the contactsPanel, and contactPanel to the WEST of the frame (and remove any old version of the contactPanel if found)
        contactsPanel.removeAll();
        JScrollPane scrollPaneContacts = new JScrollPane(contactTable);
        contactsPanel.add(scrollPaneContacts);
        frame.remove(contactsPanel);
        frame.add(contactsPanel, BorderLayout.WEST);
        
        // Update the frame
        SwingUtilities.updateComponentTreeUI(frame);
    }
	
	public void updateChatsTable(JTable chatsTable, Contact otherUser) {
		// Enable send button
		sendButton.setEnabled(true);
		
    	// Create a table model with one column for contactNames and no data initially
        DefaultTableModel tableModel = new DefaultTableModel( new Object[]{otherUser.username(), "Me"}, 0);
        Database db = Database.getInstance();
        
        // If there is no saved chatHistory linked to this ip, create a new chat table in the database
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
