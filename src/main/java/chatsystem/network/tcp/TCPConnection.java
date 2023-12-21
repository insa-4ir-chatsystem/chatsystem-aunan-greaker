package chatsystem.network.tcp;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/* Class representing an active TCP connection*/
public class TCPConnection extends Thread{
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private InetAddress ip; 
    
    public TCPConnection(InetAddress ip, Socket socket) {
    	//TODO
    }
    
    public void startConnection(InetAddress ip) {
    	//TODO
    }
    
    public void stopConnection() {
    	//TODO
    }
    // Function taking the msg from parameter and sending it to.............??????
    public void sendMessage(String msg) {
    	// TODO
    }
    
    public String readMessages() {
    	//TODO
    	return null;
    }
}
