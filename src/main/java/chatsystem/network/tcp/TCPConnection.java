package chatsystem.network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/* Class representing an active TCP connection*/
public class TCPConnection {

	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private InetAddress ip; 
    private int port;
    
    /**	Tries to create a TCPConnection with given ip and port*/
    public TCPConnection(InetAddress ip, int port) throws IOException {
    	this.ip = ip;
    	this.port = port;
    	clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    
    /** Establishes a requested TCPConnection with the end user on socket*/
    public TCPConnection(Socket socket) throws IOException {
    	clientSocket = socket;
    	this.ip = clientSocket.getInetAddress();
    	this.port = clientSocket.getPort();
    	this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    
    /**	Returns the remote ip of the host the TCPConnection is with*/
    public InetAddress getIp() {
    	return ip;
    }
    
    /**	Returns the remote port on the host that the TCPConnection is with*/
    public int getPort() {
    	return port;
    }
    
    /**	Method to check if the connection is closed*/
    public Boolean isClosed() {
    	return clientSocket.isClosed();
    }
    
    /** Closes TCPConnection */
    public void closeConnection() throws IOException {
    	clientSocket.close();
    }
    
    /**	Sends a msg line on the TCPConnection*/
    public void sendMessage(String msg) {
    	out.println(msg);
    }
    
    /**	Reads a line on the TCPConnection, returns null if no line read*/
    public String readMessage() throws IOException {
    	return in.readLine();
    }
}
