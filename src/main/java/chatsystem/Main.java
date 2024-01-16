package chatsystem;

import chatsystem.controller.TCPController;
import chatsystem.network.udp.UDPSender;
import chatsystem.ui.ChooseUsernameGUI;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.UnknownHostException;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
	
    public static void main(String[] args) throws InterruptedException, UnknownHostException {    	
    	LOGGER.trace("Found these NIC addresses: " + UDPSender.getAllCurrentIp());
        LOGGER.trace("Found these broadcast addresses: " + UDPSender.getAllBroadcastAddresses());
        Configurator.setRootLevel(Level.INFO);
        LOGGER.info("Starting ChatSystem application");

        ChooseUsernameGUI.initialize();
        TCPController.startTCPListener();
    }
}