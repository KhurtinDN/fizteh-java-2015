package ru.mipt.diht.students.elinrin.threads;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {
    private int maxQueueSize;
    private Queue<T> queue;
    private Lock queueLock = new ReentrantLock();
    private Lock offerLock = new ReentrantLock();
    private Lock takeLock = new ReentrantLock();
    private Object pushWait = new Object();
    private Object popWait = new Object();

    public final void offer(final List<T> list) {
        offerLock.lock();
        try {
            Integer last = 0;
            while (last != list.size()) {
                synchronized (popWait) {
                    while (queue.size() == maxQueueSize) {
                        try {
                            popWait.wait();
                        } catch (InterruptedException e) {
                            System.err.println(e.getMessage());
                            System.exit(1);
                        }
                    }
                    try {
                        queueLock.lock();
                        while (last < Integer.min(last + maxQueueSize - queue.size(), list.size())) {
                            queue.add(list.get(last));
                            last++;
                        }
                    } finally {
                        queueLock.unlock();
                    }
                }
            }
        } finally {
            synchronized (pushWait) {
                pushWait.notifyAll();
            }
            offerLock.unlock();
        }
    }

    public final List<Object> take(final int n) {
        takeLock.lock();
        try {
            List<Object> elements = new ArrayList<>();
            while (elements.size() != n) {
                synchronized (pushWait) {
                    while (queue.size() == 0) {
                        try {
                            pushWait.wait();
                        } catch (InterruptedException e) {
                            System.err.println(e.getMessage());
                            System.exit(1);
                        }
                    }
                    try {
                        queueLock.lock();
                        while (queue.size() > 0 && elements.size() != n) {
                            elements.add(queue.poll());
                        }
                    } finally {
                        queueLock.unlock();
                    }
                }
            }
            return elements;
        } finally {
            synchronized (popWait) {
                popWait.notifyAll();
            }
            takeLock.unlock();
        }
    }

    public BlockingQueue(final int maxSize) {
        maxQueueSize = maxSize;
        queue = new ArrayDeque<>();
    }
}
