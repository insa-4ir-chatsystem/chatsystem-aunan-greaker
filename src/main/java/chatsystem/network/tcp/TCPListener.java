package chatsystem.network.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chatsystem.controller.TCPController;
import chatsystem.network.udp.UDPListener.Observer;

/** Class representing a TCP socket that listens for new TCPConnections */
public class TCPListener extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(TCPListener.class);
	private int port; // The port on which serverSocket will be run on
	private ServerSocket serverSocket;
	private final List<Observer> observers = new ArrayList<>();
	
	public interface Observer {
		// handleNewConnection should start a thread to support multiple connections at once
		void handleNewConnection(Socket socket);
    }
	
	public TCPListener(int port) throws IOException {
		this.port = port;
	}

	// Closes ServerSocket
	public void stopServerSocket() throws IOException {
    	serverSocket.close();
    }
    
    public void addObserver(Observer obs) {
    	// Synchronized to avoid concurrent access
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }
    
    @Override
    public void run() {
    	try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			LOGGER.error("Failed to start TCPListener on port: " + port);
			e.printStackTrace();
			System.exit(1);
		}
    	// To be running as long as we are online
    	while(!serverSocket.isClosed()) {
    		Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
				
				// Synchronized to avoid concurrent access
		        synchronized (this.observers) {
		        	// Calls handler for all observers
		        	observers.forEach(obs -> obs.handleNewConnection(clientSocket));
		        }
			} catch (IOException e) {
				if (serverSocket.isClosed()) {
					LOGGER.info("TCPListener stopped");
				} else {
					LOGGER.error("Failed to accept incoming connection");
					e.printStackTrace();
				}
			}

    	}
    }
}
