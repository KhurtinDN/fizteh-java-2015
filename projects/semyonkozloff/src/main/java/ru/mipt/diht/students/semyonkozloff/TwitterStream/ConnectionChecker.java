package ru.mipt.diht.students.semyonkozloff.twitterstream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionChecker {

    public static boolean hasConnection(String hostName) {
        for (int i = 0; i < N_ATTEMPTS; ++i) {
            if (isInternetReachable(hostName)) {
                return true;
            } else {
                System.err.println("Can't connect to the server. Retrying...");
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    System.err.print("Thread can't sleep: ");
                    exception.printStackTrace(System.err);
                    System.exit(1);
                }
            }
        }
        return false;
    }

    public static boolean isInternetReachable(String hostName) {
        Socket socket = new Socket();
        InetSocketAddress socketAddress =
                new InetSocketAddress(hostName, SERVER_PORT);
        try {
            socket.connect(socketAddress, TIMEOUT);
            return true;
        } catch (IOException exception) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException junk) {
                System.err.print("Can't close socket: ");
                junk.printStackTrace(System.err);
            }
        }
    }

    private static final int N_ATTEMPTS = 5;
    private static final int RETRY_DELAY = 1000;
    private static final int SERVER_PORT = 80;
    private static final int TIMEOUT = 3000;

}
