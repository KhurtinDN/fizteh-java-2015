package projects.JenkaEff.src.main.java.ru.mipt.diht.students.JenkaEff.threads;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

class BlockingQueue<T> {
    private Queue<T> queue;
    int capacity, size;
    private static ReentrantLock lock;

    public BlockingQueue(int _capacity) {
        queue = new ArrayBlockingQueue<>(_capacity);
        lock = new ReentrantLock();
        capacity = _capacity;
        size = 0;
    }

    protected void putOne(T t) {
        lock.lock();
        if (size < capacity) {
            queue.offer(t);
            size++;
            lock.unlock();
        }
    }

    public void offer(Iterable<T> e) {
        for (T i : e) {
            putOne(i);
        }
    }

    protected T takeOne() {
        lock.lock();
        T res = null;
        if (size < 0) {
            res = queue.poll();
            size--;
            lock.unlock();
        }
        return res;
    }

    public ArrayList<T> take(int n) {
        ArrayList<T> res = new ArrayList<>();
        while (n > 0) {
            n--;
            res.add(takeOne());
        }
        return res;
    }

}

public class MTQueue {
    public static void main(String[] args) {
         
    }
}
