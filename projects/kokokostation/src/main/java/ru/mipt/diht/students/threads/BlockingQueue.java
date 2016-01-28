package ru.mipt.diht.students.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mikhail on 27.01.16.
 */
public class BlockingQueue<T> {
    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxQueueSize;
    private final Lock lock = new ReentrantLock();
    private final Condition isFull = lock.newCondition(),
        isEmpty = lock.newCondition();

    BlockingQueue(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    boolean offer(List<T> list, long timeout) throws InterruptedException {
        lock.lock();
        try {
            boolean canAdd = true;

            while (queue.size() + list.size() > maxQueueSize) {
                canAdd = isFull.await(timeout, TimeUnit.MILLISECONDS);
            }

            if(!canAdd) {
                return false;
            } else {
                queue.addAll(list);
                isEmpty.signalAll();
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    List<T> take(int elementsNum, long timeout) throws InterruptedException {
        lock.lock();
        try {
            boolean canTake = true;

            while (queue.size() < elementsNum) {
                canTake = isEmpty.await(timeout, TimeUnit.MILLISECONDS);
            }

            LinkedList<T> result = new LinkedList<>();

            if (canTake) {
                for (int i = 0; i < elementsNum; i++) {
                    result.add(queue.pollFirst());
                }
                isFull.signalAll();
            }

            return result;
        } finally {
            lock.unlock();
        }
    }
}
