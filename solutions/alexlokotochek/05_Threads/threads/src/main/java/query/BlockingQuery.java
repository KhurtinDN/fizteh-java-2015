package query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;


/**
 * Created by lokotochek on 13.12.15.
 */

class ThreadBuster extends Thread {

    // прерывает цель-поток через заданный промежуток времени

    public Thread busted;
    public long timeout;

    ThreadBuster(long time, Thread thread) {
        timeout = time;
        busted = thread;
    }

    public synchronized void interruptor() {
        busted.interrupt();
    }

    @Override
    public void run() {
        try {

            Thread.sleep(timeout);
            interruptor();

        } catch (InterruptedException e) {}
    }
}

public class BlockingQuery<T> {

    public int limit;
    public Queue<T> queue;

    public BlockingQuery(int number) {
        limit = number;
        queue = new LinkedList<T>();
    }

    public Lock lockForRead = new ReentrantLock(true);
    public Lock lockForWrite = new ReentrantLock(true);
    public Lock block = new ReentrantLock(true);
    public Condition notEmpty = block.newCondition();
    public Condition notFull = block.newCondition();

    public List<T> take(int n) {
        return take(n, 0);
    }

    public List<T> take(int n, long timeout) {
        try {
            List<T> answer = new ArrayList<T>();
            if (n == 0) {
                return answer;
            }
            if (timeout != 0) {
                ThreadBuster tb = new ThreadBuster(timeout, Thread.currentThread());
                tb.start();
            }
            lockForRead.lock();
            for (int i = 0; i < n; ++i) {
                try {
                    block.lock();
                    while (queue.size() == 0) {
                        try {
                            notEmpty.await();
                        } catch (InterruptedException e) {
                            return null;
                        }
                    }
                    answer.add(queue.poll());
                    notFull.signalAll();
                } finally {
                    block.unlock();
                }
            }
            return answer;
        } finally {
            // во что бы то ни стало освободим на чтение
            lockForRead.unlock();
        }
    }

    public void offer(List<T> list) {
        offer(list, 0);
    }

    public void offer(List<T> list, long timeout) {
        if (timeout != 0) {
            ThreadBuster tb = new ThreadBuster(timeout, Thread.currentThread());
            tb.start();
        }
        try {
            lockForWrite.lock();
            for (T element : list) {
                try {
                    block.lock();
                    while (queue.size() == limit) {
                        try {
                            notFull.await();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    queue.add(element);
                    notEmpty.signal();
                } finally {
                    block.unlock();
                }
            }
        } finally {
            // во что бы то ни стало освободим на запись
            lockForWrite.unlock();
        }
    }
}