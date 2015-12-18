package ru.mipt.diht.students.lenazherdeva.threads.blockingQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 14.12.2015.
 */
public class BlockingQueue<T> {
    private static final int LIMIT = 200;
    private Queue<T> queue = new LinkedList<>();
    private Lock addingLock = new ReentrantLock(true);  //чтобы запросы на добаввление происходили в правильном порядке
    private Lock readingLock = new ReentrantLock(true);  //чтобы запросы на чтение происходили в правильном порядке
    private Lock lockOfAccess = new ReentrantLock(true); //одновременно к массиву очереди имел доступ только один поток
    private Condition notFullQueue = lockOfAccess.newCondition();
    private Condition notEmptyQueue = lockOfAccess.newCondition();

    private int maxSize;

    public final List<T> take(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("Argument was negative number");
        }
        try {
            readingLock.lock();
            List<T> answer = new ArrayList<>();
            for (int i = 0; i < n; ++i) {
                try {
                    lockOfAccess.lock();
                    while (queue.size() == 0) {
                        try {
                            notEmptyQueue.await();
                        } catch (InterruptedException e) {
                        }
                    }
                    answer.add(queue.poll());
                    notFullQueue.signalAll();
                } finally {
                    lockOfAccess.unlock();
                }
            }
            return answer;
        } finally {
            readingLock.unlock();
        }
    }


    public final  <E extends T> void offer(List<E> list) {
        try {
            addingLock.lock();
            for (E element : list) {
                try {
                    lockOfAccess.lock();
                    while (queue.size() == maxSize) {
                        try {
                            notFullQueue.await();
                        } catch (InterruptedException e) {
                        }
                    }
                    queue.add(element);
                    notEmptyQueue.signal();
                } finally {
                    lockOfAccess.unlock();
                }
            }
        } finally {
            addingLock.unlock();
        }
    }
    public BlockingQueue(int inpMaxSize) {
        this.maxSize = inpMaxSize;
    }

    public BlockingQueue() {
        maxSize = LIMIT;
    }
}
