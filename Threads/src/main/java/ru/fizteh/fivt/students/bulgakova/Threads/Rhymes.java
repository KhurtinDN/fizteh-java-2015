package ru.fizteh.fivt.students.bulgakova.Threads;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static java.lang.Thread.currentThread;

public class Rhymes {
    private static Object lock = new Object();
    private static BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();

    public Rhymes(int threadsCount) throws InterruptedException{
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

    public class Flow implements Runnable {
        private Object lock;
        private BlockingQueue<String> blockingQueue;

        public Flow(Object _lock, BlockingQueue<String> _blockingQueue) {
            blockingQueue = _blockingQueue;
            lock = _lock;
        }

        public void run() {
            synchronized (lock) {
                try {
                    lock.wait();
                    blockingQueue.put(currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
