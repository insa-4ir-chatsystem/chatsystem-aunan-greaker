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
        Configurator.setRootLevel(Level.TRACE);
    	LOGGER.debug("Found these NIC addresses: " + UDPSender.getAllCurrentIp());
        LOGGER.debug("Found these broadcast addresses: " + UDPSender.getAllBroadcastAddresses());
        LOGGER.info("Starting ChatSystem application");

        ChooseUsernameGUI.initialize();
    }
}