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
    private Lock ourLock = new ReentrantLock();
    private Lock offerLock = new ReentrantLock();
    private Lock takeLock = new ReentrantLock();

    private int maxQueueSize;
    private int currentQueueSize = 0;

    private Condition queueNotFull = ourLock.newCondition();
    private Condition queueNotEmpty = ourLock.newCondition();

    BlockingQueue(int maXQueueSize) {
        maxQueueSize = maXQueueSize;
    }

    void offer(List<T> toInsert) throws InterruptedException {
        try {
            offerLock.lock();
            try {
                ourLock.lock();
                for (T element : toInsert) {
                    while (!(currentQueueSize + 1 < maxQueueSize)) {
                        queueNotFull.await();
                    }
                    ourQueue.add(element);
                    currentQueueSize++;
                    queueNotEmpty.signalAll();
                }
            } finally {
                ourLock.unlock();
            }
        } finally {
            offerLock.unlock();
        }
    }
    List<T> take(int howManyToTake) throws Exception {
        List<T> listToReturn = new ArrayList<>();
        try {
            takeLock.lock();
            try {
                ourLock.lock();
                for (int i = 0; i < howManyToTake; i++) {
                    while ((currentQueueSize == 0)) {
                        queueNotEmpty.await();
                    }
                    T element = ourQueue.element();
                    currentQueueSize--;
                    queueNotFull.signalAll();
                }
            } finally {
                ourLock.unlock();
            }
        } finally {
            takeLock.unlock();
        }
        return listToReturn;
    }
    void offer(List toInsert, long timeOut) {

    }
    List<T> take(int howManyToTake, long timeout) {
        return new ArrayList<>();
    }

}
