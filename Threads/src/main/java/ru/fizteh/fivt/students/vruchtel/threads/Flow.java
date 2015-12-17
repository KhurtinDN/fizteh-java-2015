package ru.fizteh.fivt.students.vruchtel.threads;

import java.util.concurrent.BlockingQueue;
import static java.lang.Thread.currentThread;

/**
 * Created by Серафима on 16.12.2015.
 */

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
