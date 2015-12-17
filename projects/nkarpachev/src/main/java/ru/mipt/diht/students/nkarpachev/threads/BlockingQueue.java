package ru.mipt.diht.students.nkarpachev.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {
    private Queue<T> queue;
    private int capacity;

    private Lock queueLock = new ReentrantLock();
    private Condition queueFull = queueLock.newCondition();
    private Condition queueEmpty = queueLock.newCondition();

    BlockingQueue(int size) {
        queue = new LinkedList<T>();
        capacity = size;
    }

    public final void offer(List<T> list) {
        queueLock.lock();
        try {
            while (queue.size() + list.size() > capacity) {
                queueFull.await();
            }
            queue.addAll(list);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
        queueEmpty.signalAll();
    }

    public final List<T> take(int amount) {
        queueLock.lock();
        List<T> answer = new LinkedList<>();
        try {
            while (queue.size() < amount) {
                queueEmpty.await();
            }
            for (int i = 0; i < amount; i++) {
                answer.add(queue.remove());
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
        queueFull.signalAll();
        return answer;
    }

    public final void offer(List<T> list, long timeout) {
        queueLock.lock();
        try {
            long lastCheckTime = System.currentTimeMillis();
            long timeToWait = timeout;
            while (queue.size() + list.size() > capacity) {
                long currentTime = System.currentTimeMillis();
                timeToWait -= (currentTime - lastCheckTime);
                lastCheckTime = currentTime;
                queueFull.await(timeToWait, TimeUnit.MILLISECONDS);
            }
            if (timeToWait > 0) {
                queue.addAll(list);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
        queueFull.signalAll();
    }

    public final List<T> take(int amount, long timeout) {
        queueLock.lock();
        List<T> answer = new LinkedList<>();
        try {
            long lastCheckTime = System.currentTimeMillis();
            long timeToWait = timeout;
            while (queue.size() < amount) {
                long currentTime = System.currentTimeMillis();
                timeToWait -= (currentTime - lastCheckTime);
                lastCheckTime = currentTime;
                queueEmpty.await(timeToWait, TimeUnit.MILLISECONDS);
            }
            if (timeToWait > 0) {
                for (int i = 0; i < amount; i++) {
                    answer.add(queue.remove());
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
        queueFull.signalAll();
        return answer;
    }
}
