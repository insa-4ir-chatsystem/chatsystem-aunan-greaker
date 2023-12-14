package chatsystem;

import chatsystem.controller.Controller;
import chatsystem.network.UDPListener;
import chatsystem.ui.View;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import java.net.SocketException;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

	public static final int BROADCAST_PORT = 7471; // The port on which all javaChatProgram instances must listen for Broadcast.
	public static final int BROADCAST_REPLY_PORT = 7472; // The port to reply to when receiving a broadcast.

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.INFO);
        LOGGER.info("Starting ChatSystem application");

        View.initialize();

        try {
            UDPListener server = new UDPListener(BROADCAST_PORT);

            server.addObserver(msg -> Controller.handleContactDiscoveryMessage(msg));

            server.start();
        } catch (SocketException e) {
            System.err.println("Could not start UDP listener: " + e.getMessage());
            System.exit(1);
        }
    }
}