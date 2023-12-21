package chatsystem.network.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/** Class containing methods for starting and stopping the */
public class TCPListener extends Thread {
	private ServerSocket serverSocket;
	
	public interface Observer {
		void handleNewConnection(InetAddress ip, Socket socket);
    }
	
	public void start(int port) throws IOException {
		// TODO
		//serverSocket = new ServerSocket(port);
    }

	// Closes ServerSocket
	public void stopServerSocket() throws IOException {
    	// TODO
    	serverSocket.close();
    }
    
    public void addObserver(Observer obs) {
    	// TODO
    }
}
