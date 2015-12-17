package ru.mipt.diht.students.feezboom.Threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * * Created by avk on 17.12.15.
 **/

@SuppressWarnings("checkstyle:designforextension")
public class BlockingQueue<T> {
    private Queue<T> ourQueue = new LinkedList<>();
    private Lock insertionLock = new ReentrantLock();
    private Lock removingLock = new ReentrantLock();
    private int maxQueueSize;
    private int currentQueueSize = 0;

    private Condition queueNotFull = insertionLock.newCondition();
    private Condition queueNotEmpty = removingLock.newCondition();

    BlockingQueue(int maXQueueSize) {
        maxQueueSize = maXQueueSize;
    }

    void offer(List<T> toInsert) throws InterruptedException {
        try {
            insertionLock.lock();
            for (T element : toInsert) {
                if (currentQueueSize + 1 < maxQueueSize) {
                    ourQueue.add(element);
                    currentQueueSize++;
                    queueNotEmpty.signalAll();
                } else {
                    queueNotFull.await();
                }
            }
        } finally {
            insertionLock.unlock();
        }
    }
    List<T> take(int howManyToTake) throws Exception {
        List<T> listToReturn = new ArrayList<>();
        try {
            removingLock.lock();
            for (int i = 0; i < howManyToTake; i++) {
                if (currentQueueSize > 0) {
                    T element = ourQueue.element();
                    currentQueueSize--;
                    queueNotFull.signalAll();
                } else {
                    queueNotEmpty.await();
                }
            }
        } finally {
            removingLock.unlock();
        }
        return listToReturn;
    }
    void offer(List toInsert, long timeOut) {

    }
    List<T> take(int howManyToTake, long timeout) {
        return new ArrayList<>();
    }

}
