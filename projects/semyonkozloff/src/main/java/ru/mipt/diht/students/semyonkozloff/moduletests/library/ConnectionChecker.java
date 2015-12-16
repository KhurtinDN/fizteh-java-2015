package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionChecker {

    private static final int N_ATTEMPTS = 5;
    private static final int RETRY_DELAY = 1000;
    private static final int SERVER_PORT = 80;
    private static final int TIMEOUT = 3000;

    public static boolean hasConnection(String hostName)
            throws InterruptedException, IOException {
        for (int i = 0; i < N_ATTEMPTS; ++i) {
            if (isInternetReachable(hostName)) {
                return true;
            } else {
                Thread.sleep(RETRY_DELAY);
            }
        }
        return false;
    }

    public static boolean isInternetReachable(String hostName)
            throws IOException {
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
            } catch (IOException exception) {
                IOException ioException =
                        new IOException("Can't close socket.");
                ioException.initCause(exception);
                throw ioException;
            }
        }
    }
}
