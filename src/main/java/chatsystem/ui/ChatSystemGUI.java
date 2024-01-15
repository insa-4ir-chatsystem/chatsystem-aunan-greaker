package chatsystem.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.contacts.Contact;
import chatsystem.contacts.ContactList;
import chatsystem.log.Database;
import chatsystem.log.TableAlreadyExists;
import chatsystem.network.udp.UDPSender;

public class ChatSystemGUI {
	private JTable contactTable;
	private JTable chatsTable;
	
	private static final Logger LOGGER = LogManager.getLogger(ChatSystemGUI.class);
	
	public ChatSystemGUI(String username) {
		final JFrame frame = new JFrame();
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contactTable = new JTable();
        updateContactTable(contactTable);
        contactTable.setPreferredScrollableViewportSize(new Dimension(200, 500));
        JScrollPane scrollPaneContacts = new JScrollPane(contactTable);
        panel.add(scrollPaneContacts);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        chatsTable = new JTable();
        chatsTable.setPreferredScrollableViewportSize(new Dimension(400, 460));
        JScrollPane scrollPaneChats = new JScrollPane(chatsTable);
        panel.add(scrollPaneChats);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Write your message here: "));
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(600, 25));
        panel.add(messageField);

        gbc.gridx = 1;
        gbc.gridy = 3;
	    JButton sendButton = new JButton("Send");
	    panel.add(sendButton);

	    sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	            //String message = messageField.getText(); Kommenterte ut fordi eclipse ga warning
	        }
	    });

        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("ChatSystem");
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void updateContactTable(JTable contactTable) {
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
    }
	
	public void updateChatsTable(JTable chatsTable, Contact otherUser) {
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
        
        // Database controller???
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
	
	        // Set the table model for the JTable
	        chatsTable.setModel(tableModel);
	        
	        // Make the entire table non-editable
	        chatsTable.setEnabled(false);
        } catch (SQLException e) {
        	LOGGER.error(e.getMessage());
        }
    }
}
