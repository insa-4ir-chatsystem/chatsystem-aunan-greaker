package chatsystem.network.udp;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UDPTests {
    private static final int TEST_PORT = 1871;
    private static final int SLEEP_DELAY = 100; // If test fails, increase this value

   @Test
    void sendReceiveTest() throws Exception {
        List<String> testMessages = Arrays.asList("alice", "bob", "chloe", "multi\nline string", "éàç");

        List<String> receivedMessages = new ArrayList<>();
        UDPListener listener = new UDPListener(TEST_PORT);
        listener.addObserver(message -> {
            receivedMessages.add(message.text());
        });
        listener.start();

        for (String msg : testMessages) {
            UDPSender.send(InetAddress.getLoopbackAddress(), TEST_PORT, msg);
        }

        Thread.sleep(SLEEP_DELAY);
        assertEquals(testMessages, receivedMessages);
    }
}