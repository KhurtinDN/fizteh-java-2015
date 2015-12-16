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

    public int size() {
        return data.size();
    }

    synchronized public void offer(List<T> e) {
        while (data.size() + e.size() > maxQueueSize) {
            Thread.yield();
        }
        data.addAll(e);
    }

    synchronized public void offer(List<T> e, long timeout) {
        timeout += System.currentTimeMillis();
        while ((data.size() + e.size() > maxQueueSize) && (timeout - System.currentTimeMillis()) > 0) {
            Thread.yield();
        }
        if (timeout > 0) {
            data.addAll(e);
        }
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

    synchronized List<T> take(int n, long timeout) {
        timeout += System.currentTimeMillis();
        while ((data.size() < n) && (timeout - System.currentTimeMillis()) > 0) {
            Thread.yield();
        }
        if (timeout > 0) {
            List<T> answer = new ArrayList<T>();
            for (int i = 0; i < n; i++) {
                answer.add(data.remove());
            }
            return answer;
        }
        return null;
    }
}
