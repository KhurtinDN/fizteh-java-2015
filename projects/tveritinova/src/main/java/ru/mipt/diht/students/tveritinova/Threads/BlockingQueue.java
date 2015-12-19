package ru.mipt.diht.students.tveritinova.Threads;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {
    private int maxQueueSize;
    private Queue<T> queue;
    private Lock offerLock = new ReentrantLock();
    private Lock queueLock = new ReentrantLock();
    private Lock takeLock = new ReentrantLock();

    private Object takeWait = new Object();
    private Object offerWait = new Object();

    public BlockingQueue(int size) {
        maxQueueSize = size;
        queue = new ArrayDeque<T>();
    }

    public final void offer(List<T> elements) {
        offerLock.lock();
        try {
            int offeredElementsNumber = 0;
            while (offeredElementsNumber != elements.size()) {
                synchronized (takeWait) {
                    while (queue.size() == maxQueueSize) {
                        try {
                            takeWait.wait();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    try {
                        queueLock.lock();
                        while (offeredElementsNumber < elements.size()
                                && queue.size() < maxQueueSize) {
                            queue.add(elements.get(offeredElementsNumber));
                            ++offeredElementsNumber;
                        }
                    } finally {
                        queueLock.unlock();
                    }
                }
            }
        } finally {
            synchronized (offerWait) {
                offerWait.notifyAll();
            }
            offerLock.unlock();
        }
    }

    public final List<T> take(int numberOfElements) {
        takeLock.lock();
        try {
            List<T> answer = new ArrayList<>();
            while (answer.size() != numberOfElements) {
                synchronized (offerWait) {
                    while (queue.size() < numberOfElements - answer.size()) {
                        try {
                            offerWait.wait();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    try {
                        queueLock.lock();
                        while (answer.size() < numberOfElements
                                && queue.size()
                                >= numberOfElements - answer.size()) {
                            answer.add(queue.poll());
                        }
                    } finally {
                        queueLock.unlock();
                    }
                }
            }
            return answer;
        } finally {
            synchronized (takeWait) {
                takeWait.notifyAll();
            }
            takeLock.unlock();
        }
    }
}
