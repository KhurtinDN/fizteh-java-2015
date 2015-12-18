package threads.task3;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockingQueue<T> {
    private int maxElements;

    private List<T> queue;

    private ReentrantLock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public BlockingQueue(int max) {
        if (maxElements <= 0) {
            throw new IllegalArgumentException("maxElements must be positive");
        }
        this.maxElements = max;
        queue = new LinkedList<>();
    }


    public void offer(List<T> elements) {
        lock.lock();
        try {
            while (!checkEmptyEnough(elements.size())) {
                notFull.await();
            }

            queue.addAll(elements);
        } catch (InterruptedException e) {
            // ignore
        } finally {

            notEmpty.signal();

            lock.unlock();
        }
    }


    private boolean checkEmptyEnough(int requiredSize) {
        return (queue.size() + requiredSize) <= maxElements;
    }


    public List<T> take(int countOfElements) {
        lock.lock();
        try {
            while (!checkFullEnough(countOfElements)) {
                notEmpty.await();
            }
            int lastElementIndex = queue.size();
            int firstElementIndex = lastElementIndex - countOfElements;
            List<T> result = new LinkedList<>(queue.subList(firstElementIndex, lastElementIndex));
            queue.removeAll(result);
            return result;
        } catch (InterruptedException e) {
            // ignore
            return Collections.emptyList();
        } finally {

            notFull.signal();
            lock.unlock();
        }
    }

    private boolean checkFullEnough(int countOfElements) {
        return queue.size() >= countOfElements;
    }
}
