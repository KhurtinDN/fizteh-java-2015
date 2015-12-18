package ru.mipt.diht.students.glutolik.Threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by glutolik on 14.12.15.
 */
public class BlockingQueue<T> {
    private volatile Queue<T> queue = new LinkedList<>();
    private final int maxElements;
    private Lock lock = new ReentrantLock();

    public BlockingQueue(int number) {
        maxElements = number;
    }

    public final void offer(List<T> newMembers) throws InterruptedException, IllegalArgumentException {
        lock.lock();
        boolean freeSpace = true;
        try {
            synchronized (queue) {
                while (newMembers.size() + queue.size() > maxElements) {
                    queue.wait();
                }
                queue.addAll(newMembers);
            }
        } finally {
            lock.unlock();
        }
    }

    public final List<T> take(int numberOfMembers) throws IllegalArgumentException, InterruptedException {
        lock.lock();
        try {
            if (numberOfMembers > maxElements) {
                throw new IllegalArgumentException("You want to take too many members");
            }
            List<T> taken = new ArrayList<>();
            synchronized (queue) {
                while (numberOfMembers > queue.size()) {
                    queue.wait();
                }
                for (int i = 0; i < numberOfMembers; i++) {
                    taken.add(queue.poll());
                }
            }
            return taken;
        } finally {
            lock.unlock();
        }
    }
}
