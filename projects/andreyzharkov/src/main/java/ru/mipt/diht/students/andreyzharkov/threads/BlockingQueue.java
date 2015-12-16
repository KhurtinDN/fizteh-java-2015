package ru.mipt.diht.students.andreyzharkov.threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Андрей on 15.12.2015.
 */
public class BlockingQueue<T> {
    private final int maxSize;
    private Lock lock;
    private Condition notEmpty;
    private Condition notFull;
    private Queue<T> elements;

    public BlockingQueue(int maxQueueSize) {
        maxSize = maxQueueSize;
        elements = new LinkedList<>();
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    public final void offer(List<T> newElements, long timeout) throws InterruptedException, TimeoutException {
        lock.lock();
        try {
            boolean canAdd = true;
            if (timeout == 0) {
                while (elements.size() + newElements.size() > maxSize){
                    notFull.await();
                }
            } else {
                long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
                while (elements.size() + newElements.size() > maxSize && nanos > 0) {
                    nanos = notFull.awaitNanos(nanos);
                    if (nanos < 0) {
                        canAdd = false;
                    }
                }
            }

            if (canAdd) {
                elements.addAll(newElements);
                if (newElements.size() > 0) {
                    notEmpty.signal();
                }
            } else {
                throw new TimeoutException("timeout was exceeded");
            }
        } finally {
            lock.unlock();
        }
    }

    public final void offer(List<T> newElements) throws InterruptedException, TimeoutException {
        offer(newElements, 0);
    }


    public final List<T> take(int count, long timeout) throws InterruptedException {
        lock.lock();
        try {
            if (timeout == 0) {
                while (elements.size() < count){
                    notEmpty.await();
                }
            } else {
                long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
                while (elements.size() < count && nanos > 0) {
                    nanos = notEmpty.awaitNanos(nanos);
                    if (nanos < 0) {
                        return null;
                    }
                }
            }

            List<T> asked = new ArrayList<>();
            for (int i = 0; i < count; ++i) {
                asked.add(elements.poll());
            }
            if (count > 0) {
                notFull.signal();
            }
            return asked;
        } finally {
            lock.unlock();
        }
    }

    public final List<T> take(int count) throws InterruptedException {
        return take(count, 0);
    }
}
