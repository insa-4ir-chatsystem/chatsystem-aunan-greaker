package chatsystem.network.tcp;

import java.net.*;
import java.io.*;

public class TCPConnection extends Thread {
	private Socket connectionSocket;
	private PrintWriter out;
    private BufferedReader in;
    
    @Override
    public void run() {
    	//TODO
    }
}
