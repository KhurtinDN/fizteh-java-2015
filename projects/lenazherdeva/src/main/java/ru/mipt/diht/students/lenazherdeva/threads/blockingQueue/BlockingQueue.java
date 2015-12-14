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

    public final List<T> take(int n) throws InterruptedException, IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("There should be positive number as command line argument");
        }
        if (n == 0) {
            return new ArrayList<T>();
        }
        readingLock.lockInterruptibly();
        try {
            lockOfAccess.lockInterruptibly();
            try {
                List<T> answer = new ArrayList<>();
                while (queue.size() < n) {
                    notEmptyQueue.await();
                }
                for (int i = 0; i < queue.size(); ++i) {
                    answer.add(queue.poll());
                }
                notFullQueue.signalAll(); //теперь очередь не заполнена
                return answer;
            } finally {
                lockOfAccess.unlock();
            }
        } finally {
         readingLock.unlock();
        }
    }

    public final <E extends T> boolean offer(List<E> list) throws InterruptedException {
        addingLock.lockInterruptibly();
        try {
            lockOfAccess.lockInterruptibly();
            try {
                while (queue.size() + list.size() > maxSize) {
                    notFullQueue.await();
                }
                queue.addAll(list);
                notEmptyQueue.signal();
            } finally {
                lockOfAccess.unlock();
            }
        } finally {
            addingLock.unlock();
        }
        return true;
    }

    public BlockingQueue(int inpMaxSize) {
        this.maxSize = inpMaxSize;
    }

    public BlockingQueue() {
        maxSize = LIMIT;
    }
}
