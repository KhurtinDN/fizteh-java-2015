package ru.fizteh.fivt.students.krakhmalev.Threads;



import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {

    private Queue<T> queue;
    private int maxSize;
    private final Lock lock = new ReentrantLock();
    private final Condition notEnoughSpace = lock.newCondition();
    private final Condition notEnoughElements = lock.newCondition();
    private final Object offerSynchronizer = new Object();
    private final Object takeSynchronizer = new Object();

    BlockingQueue(int size) {
        queue = new ArrayDeque<T>();
        maxSize = size;
    }

    void offer(List<T> toAdd) throws InterruptedException {
        synchronized (offerSynchronizer) {
            lock.lock();
            try {
                while ((queue.size() + toAdd.size()) > maxSize) {
                    notEnoughSpace.await();
                }
                queue.addAll(toAdd);
                notEnoughElements.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    List<T> take(int n) throws InterruptedException {
        synchronized (takeSynchronizer) {
            lock.lock();
            List<T> ans = new ArrayList<T>();
            try {

                while (queue.size() < n) {
                    notEnoughElements.await();
                }
                for (int i = 0; i < n; ++i) {
                    ans.add(queue.remove());
                }
                notEnoughElements.signalAll();
            } finally {
                lock.unlock();
                return ans;
            }
        }
    }

    void offer(List<T> toAdd, long timeout) throws  InterruptedException {
        synchronized (offerSynchronizer) {
            lock.lock();
            long waitingTime = timeout;
            final long startTime = System.currentTimeMillis();
            try {
                while (queue.size() + toAdd.size() > maxSize && waitingTime > 0) {
                    notEnoughElements.await(waitingTime, TimeUnit.MILLISECONDS);
                    waitingTime = timeout - (System.currentTimeMillis() - startTime);
                }
                if (queue.size() + toAdd.size() <= maxSize) {
                    queue.addAll(toAdd);
                    notEnoughElements.notifyAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    List<T> take(int n, long timeout) throws InterruptedException {
        synchronized (takeSynchronizer) {
            lock.lock();
            List<T> ans = new ArrayList<T>();
            long waitingTime = timeout;
            final long startTime = System.currentTimeMillis();
            try {
                while (queue.size() < n && waitingTime > 0) {
                    notEnoughElements.await(waitingTime, TimeUnit.MILLISECONDS);
                    waitingTime = timeout - (System.currentTimeMillis() - startTime);
                }
                if (queue.size() >= n) {
                    for (int i = 0; i < n; ++i) {
                        ans.add(queue.remove());
                    }
                    notEnoughElements.notifyAll();
                }
            } finally {
                lock.unlock();
                return ans;
            }
        }
    }
}

