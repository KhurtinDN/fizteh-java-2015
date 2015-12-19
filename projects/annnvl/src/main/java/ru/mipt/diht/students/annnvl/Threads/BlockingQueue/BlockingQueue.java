package ru.mipt.diht.students.annnvl.Threads.BlockingQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockingQueue<T> {
    private final int maxQueueSize;
    private Queue<T> data;

    BlockingQueue(int size) {
        maxQueueSize = size;
        data = new LinkedList<T>();
    }

    public final int size() {
        return data.size();
    }

    synchronized void offer(List<T> e) {
        while (data.size() + e.size() > maxQueueSize) {
            Thread.yield();
        }
        data.addAll(e);
    }

    synchronized void offer(List<T> e, long timeout) throws InterruptedException{
        timeout += System.currentTimeMillis();
        while ((data.size() + e.size() > maxQueueSize) && (timeout - System.currentTimeMillis()) > 0) {
            wait(timeout - System.currentTimeMillis());
        }
        if (timeout > 0) {
            data.addAll(e);
        }
        notifyAll();
    }

    synchronized List<T> take(int n) {
        while (data.size() < n) {
            Thread.yield();
        }
        List<T> answer = new ArrayList<T>();
        for (int i = 0; i < n; i++) {
            answer.add(data.remove());
        }
        return answer;
    }

    synchronized List<T> take(int n, long timeout) throws InterruptedException{
        timeout += System.currentTimeMillis();
        while ((data.size() < n) && (timeout - System.currentTimeMillis()) > 0) {
            wait(timeout - System.currentTimeMillis());
        }
        if (timeout > 0) {
            List<T> answer = new ArrayList<T>();
            for (int i = 0; i < n; i++) {
                answer.add(data.remove());
            }
            notifyAll();
            return answer;
        }
        notifyAll();
        return null;
    }
}
