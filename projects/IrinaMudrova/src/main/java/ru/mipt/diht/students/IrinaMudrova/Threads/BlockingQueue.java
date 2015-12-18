
package ru.mipt.diht.students.IrinaMudrova.Threads;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ирина on 15.12.2015.
 */

public class BlockingQueue<T> {
    private int maxQueueSize;
    private Queue<T> queue;
    private Lock queueLock = new ReentrantLock();
    private Lock offerLock = new ReentrantLock();
    private Lock takeLock = new ReentrantLock();
    private Object pushWait = new Object();
    private Object popWait = new Object();

    public void offer(List<T> list) {
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

    public List<Object> take(int n) {
        takeLock.lock();
        try {
            List<Object> ans = new ArrayList<>();
            while (ans.size() != n) {
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
                        while (queue.size() > 0 && ans.size() != n) {
                            ans.add(queue.poll());
                        }
                    } finally {
                        queueLock.unlock();
                    }
                }
            }
            return ans;
        } finally {
            synchronized (popWait) {
                popWait.notifyAll();
            }
            takeLock.unlock();
        }
    }

    public BlockingQueue(int maxSize) {
        maxQueueSize = maxSize;
        queue = new ArrayDeque<T>();
    }
}
