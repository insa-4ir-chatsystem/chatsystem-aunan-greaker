package chatsystem.network.udp;

import org.junit.jupiter.api.Test;

import chatsystem.controller.UDPController;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UDPTests {
    private static final int SLEEP_DELAY = 200; // If test fails, increase this value

   @Test
    void sendReceiveTest() throws Exception {
        List<String> testMessages = Arrays.asList("alice", "bob", "chloe", "multi\nline string", "éàç");

        List<String> receivedMessages = new ArrayList<>();
        UDPListener listener = new UDPListener(UDPController.BROADCAST_PORT);
        listener.addObserver(message -> {
            receivedMessages.add(message.text());
        });
        listener.start();

        for (String msg : testMessages) {
            UDPSender.send(InetAddress.getLoopbackAddress(), UDPController.BROADCAST_PORT, msg);
        }

        Thread.sleep(SLEEP_DELAY);
        assertEquals(testMessages, receivedMessages);
    }
}