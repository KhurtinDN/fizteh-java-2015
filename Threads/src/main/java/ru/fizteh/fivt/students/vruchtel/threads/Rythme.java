package ru.fizteh.fivt.students.vruchtel.threads;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Rythme {
    private static Object lock = new Object();
    private static BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();

    public Rythme(int threadsCount) throws InterruptedException{
        ArrayList<Thread> threads = new ArrayList<Thread>(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            threads.add(new Thread(new Flow(lock, blockingQueue), "Thread-" + i));
            (threads.get(i)).start();
            Thread.sleep(100);
        }

        Thread.sleep(100);

        for (int i = 0; i < threadsCount; i++) {
            synchronized (lock) {
                lock.notify();
            }
            System.out.println(" " + blockingQueue.take());
            threads.get(i).join();
        }
    }

}