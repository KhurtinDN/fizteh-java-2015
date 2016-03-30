package ru.mipt.diht.students.semyonkozloff.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockingQueue<T> {

    private Queue<T> queue;
    private int size;

    private Lock queueLock = new ReentrantLock();
    private Condition queueIsFull = queueLock.newCondition();
    private Condition queueIsEmpty = queueLock.newCondition();

    BlockingQueue(int initialSize) {
        queue = new LinkedList<>();
        this.size = initialSize;
    }

    public void offer(List<T> list) throws InterruptedException {
        queueLock.lock();
        try {
            while (queue.size() + list.size() > size) {
                queueIsFull.await();
            }

            queue.addAll(list);
        } catch (InterruptedException exception) {
            InterruptedException interruptedException =
                    new InterruptedException("Thread was interrupted");
            interruptedException.initCause(exception);
            throw interruptedException;
        } finally {
            queueLock.unlock();
        }
        queueIsEmpty.signalAll();
    }

    public List<T> take(int nRequestedElements) throws InterruptedException {
        queueLock.lock();
        List<T> requestedList = new LinkedList<>();
        try {
            while (queue.size() < nRequestedElements) {
                queueIsEmpty.await();
            }

            for (int i = 0; i < nRequestedElements; ++i) {
                requestedList.add(queue.remove());
            }
        } catch (InterruptedException exception) {
            InterruptedException interruptedException =
                    new InterruptedException("Thread was interrupted");
            interruptedException.initCause(exception);
            throw interruptedException;
        } finally {
            queueLock.unlock();
        }
        queueIsFull.signalAll();
        return requestedList;
    }
}
