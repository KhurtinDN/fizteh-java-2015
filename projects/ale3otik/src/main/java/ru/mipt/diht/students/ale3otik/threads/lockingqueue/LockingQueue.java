package ru.mipt.diht.students.ale3otik.threads.lockingqueue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by alex on 06.12.15.
 */
public class LockingQueue<E> {
    private final Object synchronizer = new Object();
    private final Object queueAccesSynchronizer = new Object();
    private final Object offerCounterSync = new Object();
    private final Object takeCounterSync = new Object();

    private volatile long currentOfferNumber;
    private volatile long currentTakeNumber;
    private volatile long nextOfferNumber;
    private volatile long nextTakeNumber;
    private volatile int maxQueueSize;
    private volatile List<E> queue;

    private void addTheList(List<E> listToAdd, long timeout) throws InterruptedException {
        long endTime = System.currentTimeMillis() + timeout;
        boolean isTimeoutBreakSet = timeout > 0 ? true : false;
        synchronized (synchronizer) {
            long myActNum;
            synchronized (offerCounterSync) {
                myActNum = nextOfferNumber;
                ++nextOfferNumber;
                if (nextOfferNumber == Long.MAX_VALUE) {
                    nextOfferNumber = 0;
                }
            }

            while (true) {
                synchronized (queueAccesSynchronizer) {
                    if (myActNum == currentOfferNumber
                            && queue.size() + listToAdd.size() <= maxQueueSize) {
                        break;
                    }
                }

                long time = endTime - System.currentTimeMillis();
                if (!isTimeoutBreakSet) {
                    synchronizer.wait();
                } else if (time > 0) {
                    synchronizer.wait(time);
                } else {
                    ++currentOfferNumber;
                    synchronizer.notifyAll();
                    return;
                }
            }

            synchronized (queueAccesSynchronizer) {
                queue.addAll(listToAdd);
            }

            ++currentOfferNumber;
            synchronizer.notifyAll();
        }
    }

    private List<E> getTheList(int lengthToTake, long timeout) throws InterruptedException {
        long endTime = System.currentTimeMillis() + timeout;

        boolean isTimeoutBreakSet = timeout > 0 ? true : false;
        List<E> answer = null;
        synchronized (synchronizer) {
            long myActNum;
            synchronized (takeCounterSync) {
                myActNum = nextTakeNumber;
                ++nextTakeNumber;
                if (nextTakeNumber == Long.MAX_VALUE) {
                    nextTakeNumber = 0;
                }
            }
            while (true) {
                synchronized (queueAccesSynchronizer) {
                    if (myActNum == currentTakeNumber && lengthToTake <= queue.size()) {
                        break;
                    }
                }
                long time = endTime - System.currentTimeMillis();
                if (!isTimeoutBreakSet) {
                    synchronizer.wait();
                } else if (time > 0) {
                    synchronizer.wait(time);
                } else {
                    ++currentTakeNumber;
                    synchronizer.notifyAll();
                    return null;
                }
            }

            synchronized (queueAccesSynchronizer) {
                answer = new LinkedList<E>(queue.subList(0, lengthToTake));
                queue.subList(0, lengthToTake).clear();
            }

            ++currentTakeNumber;
            synchronizer.notifyAll();
            return answer;
        }
    }

    //public methods
    public LockingQueue(int maxSize) {
        queue = new LinkedList<>();
        this.maxQueueSize = maxSize;
        currentOfferNumber = 0;
        currentTakeNumber = 0;
        nextOfferNumber = 0;
        nextTakeNumber = 0;
    }

    public final void offer(List<E> toAdd) throws InterruptedException {
        addTheList(toAdd, -1);
    }

    public final void offer(List<E> toAdd, long timeout) throws InterruptedException{
        addTheList(toAdd, timeout);
    }

    public final List<E> take(int n) throws InterruptedException{
        if (n == 0) {
            return new LinkedList<>();
        }
        return getTheList(n, -1);
    }

    public final List<E> take(int n, long timeout) throws InterruptedException{
        if (n == 0) {
            return new LinkedList<>();
        }
        return getTheList(n, timeout);
    }
}
