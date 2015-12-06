package ru.mipt.diht.students.ale3otik.threads.lockingqueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 06.12.15.
 */
public class LockingQueue<E> {
    private final Object synchronizer = new Object();
    private final Object queueAccesSynchronizer = new Object();

    private volatile long currentOfferNumber;
    private volatile long currentTakeNumber;
    private long nextOfferNumber;
    private long nextTakeNumber;
    private volatile int maxQueueSize;
    private List<E> queue;

    private class TimeInterrupter extends Thread {
        private long timeout;
        private Thread pthread;
        TimeInterrupter(long rcvTimeout, Thread threadToWakeUp) {
            this.timeout = rcvTimeout;
            this.pthread = threadToWakeUp;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(timeout);
                this.pthread.interrupt();
            } catch (InterruptedException e) {
            }
        }
    }

    private void addTheList(List<E> listToAdd, long timeout) {
        TimeInterrupter interrupter = null;
        if(timeout >= 0) {
            interrupter = new TimeInterrupter(timeout,Thread.currentThread());
            interrupter.start();
        }
        synchronized (synchronizer) {
            try {
                long myActNum = nextOfferNumber;
                ++nextOfferNumber;
                if (nextOfferNumber == Long.MAX_VALUE) {
                    nextOfferNumber = 0;
                }

                while (myActNum != currentOfferNumber
                        || queue.size() + listToAdd.size() > maxQueueSize) {
                    synchronizer.wait();
                }

                synchronized (queueAccesSynchronizer) {
                    queue.addAll(new ArrayList(listToAdd));
                }

                if (interrupter != null) {
                    interrupter.interrupt();
                }
                ++currentOfferNumber;
                synchronizer.notifyAll();
            } catch (InterruptedException e) {
                ++currentOfferNumber;
                return;
            }
        }
    }

    private List<E> getTheList(int lengthToTake, long timeout) {
        TimeInterrupter interrupter = null;
        if(timeout >= 0) {
            interrupter = new TimeInterrupter(timeout,Thread.currentThread());
            interrupter.start();
        }
        synchronized (synchronizer) {
            try {
                long myActNum = nextTakeNumber;
                ++nextTakeNumber;
                if (nextTakeNumber == Long.MAX_VALUE) {
                    nextTakeNumber = 0;
                }

                while (myActNum != currentTakeNumber || lengthToTake > queue.size()) {
                    synchronizer.wait();
                }

                List<E> answer;
                synchronized (queueAccesSynchronizer) {
                    answer = (List) queue.subList(0, lengthToTake);
                    queue = queue.subList(lengthToTake, queue.size());
                }


                if (interrupter != null) {
                    interrupter.interrupt();
                }

                ++currentTakeNumber;
                synchronizer.notifyAll();

                return answer;

            } catch (InterruptedException e) {
                ++currentTakeNumber;
                synchronizer.notifyAll();
                return null;
            }
        }
    }

    //public methods
    public LockingQueue(int maxSize) {
        queue = new ArrayList<>();
        this.maxQueueSize = maxSize;
        currentOfferNumber = 0;
        currentTakeNumber = 0;
        nextOfferNumber = 0;
        nextTakeNumber = 0;
    }

    public final void offer(List<E> toAdd) {
        addTheList(toAdd, -1);
    }

    public final void offer(List toAdd, long timeout) {
        addTheList(toAdd, -1);
    }

    public final List<E> take(int n) {
        if (n == 0) {
            return new ArrayList<>();
        }
        return getTheList(n, -1);
    }

    public final List<E> take(int n, long timeout) {
        if (n == 0) {
            return new ArrayList<>();
        }
        return getTheList(n, timeout);
    }
}
