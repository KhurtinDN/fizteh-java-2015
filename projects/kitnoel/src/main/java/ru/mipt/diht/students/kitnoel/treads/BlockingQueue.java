package ru.mipt.diht.students.kitnoel.treads;

/**
 * Created by leonk on 19.12.15.
 */
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockingQueue<T> {
    private int maxQueueSize;
    private Queue<T> queue;
    BlockingQueue(int maxSize) {
        maxQueueSize = maxSize;
        queue = new LinkedList<>();
    }
    public synchronized void offer(List<T> L) throws InterruptedException {
        while (queue.size() + L.size() > maxQueueSize) {
            wait();
            Thread.sleep(100);
        }
        for (int i = 0; i < L.size(); ++i) {
            queue.add(L.get(i));
        }
        notifyAll();
    }
    public synchronized List<T> take(int n) throws InterruptedException {
        while (queue.size() < n) {
            wait();
            Thread.sleep(100);
        }
        List<T> ans = new LinkedList<>();
        for (int i = 0; i < n; ++i) {
            ans.add(queue.remove());
        }
        notifyAll();
        return ans;
    }
}
