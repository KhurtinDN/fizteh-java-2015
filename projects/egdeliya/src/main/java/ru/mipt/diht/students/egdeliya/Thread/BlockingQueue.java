package ru.mipt.diht.students.egdeliya.Thread;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Эгделия on 17.12.2015.
 */
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
    }

    //добавление элементов
    public final void offer(List<T> elements) {
        offerLock.lock();
        try {
            int offeredElementsNumber = 0;
            while (offeredElementsNumber != elements.size()) {
                synchronized (takeWait) {
                    //пока не можем добавить элементы
                    while (queue.size() == maxQueueSize) {
                        try {
                            //ждём, пока кто-нибудь вытащит элемент
                            takeWait.wait();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    //можем вставить элемент
                    try {
                        //вставляем атомарно, т.е блокируем очередь
                        queueLock.lock();
                        while (offeredElementsNumber < elements.size() && queue.size() < maxQueueSize) {
                            queue.add(elements.get(offeredElementsNumber));
                            ++offeredElementsNumber;
                        }
                    } finally {
                        queueLock.unlock();
                    }
                }
            }
        } finally {
            //будим всех, кто хочет добавить элементы
            synchronized (offerWait) {
                offerWait.notifyAll();
            }
            offerLock.unlock();
        }
    }

    //возвращает numberOfElements первых элементов из очереди
    public final List<T> take(int numberOfElements) {
        takeLock.lock();
        try {
            List<T> answer = new ArrayList<>();
            while (answer.size() != numberOfElements) {
                synchronized (offerWait) {
                    //пока количество элементов в очереди меньше, чем numberOfElements
                    while (queue.size() < numberOfElements - answer.size()) {
                        try {
                            offerWait.wait();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    //можем удалить элемент
                    try {
                        queueLock.lock();
                        while (answer.size() < numberOfElements
                                &&
                                queue.size() >= numberOfElements - answer.size()) {
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
