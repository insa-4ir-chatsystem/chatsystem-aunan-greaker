package chatsystem.network.tcp;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Class containing method for sending TCP-messages (chatsystem messages to contact)*/
public class TCPClient {
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    
    // Function taking the msg from parameter and sending it to.............??????
    public static void sendMessage(String msg) {
    	// TODO
    }
}
