package chatsystem.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Class representing a TCP socket that listens for new TCPConnections */
public class TCPListener extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(TCPListener.class);
	private int port; // The port on which serverSocket will be run on
	private ServerSocket serverSocket;
	private final List<Observer> observers = new ArrayList<>();
	
	public interface Observer {
		/**	Method to be called when a new connection comes in */
		void handleNewConnection(Socket socket);
    }
	
	public TCPListener(int port) throws IOException {
		this.port = port;
	}

	/** Closes the ServerSocket*/
	public void close() throws IOException {
    	serverSocket.close();
    }
    
	/**	Adds observer to list of observers*/
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
			LOGGER.fatal("Failed to start TCPListener on port: " + port + " because: " + e.getMessage());
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
					// ServerSocket closed, no need to print stack trace
				} else {
					LOGGER.error("Failed to accept incoming connection because: " + e.getMessage());
				}
			}
    	}
		LOGGER.trace("TCPListener closed");
    }
}
