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
        if (timeout >= 0) {
            interrupter = new TimeInterrupter(timeout, Thread.currentThread());
            interrupter.start();
        }
        synchronized (synchronizer) {
            try {
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
                    synchronizer.wait();
                }

                synchronized (queueAccesSynchronizer) {
                    queue.addAll(new LinkedList(listToAdd));
                }

                if (interrupter != null) {
                    interrupter.interrupt();
                }

            } catch (InterruptedException e) {
            }
            ++currentOfferNumber;
            synchronizer.notifyAll();
        }
    }

    private List<E> getTheList(int lengthToTake, long timeout) {
        TimeInterrupter interrupter = null;
        if (timeout >= 0) {
            interrupter = new TimeInterrupter(timeout, Thread.currentThread());
            interrupter.start();
        }
        List<E> answer = null;
        synchronized (synchronizer) {
            try {
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
                    synchronizer.wait();
                }

                synchronized (queueAccesSynchronizer) {
                    answer = new LinkedList<E>(queue.subList(0, lengthToTake));
                    queue.subList(0, lengthToTake).clear();
                }

                if (interrupter != null) {
                    interrupter.interrupt();
                }

            } catch (InterruptedException e) {
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

    public final void offer(List<E> toAdd) {
        addTheList(toAdd, -1);
    }

    public final void offer(List<E> toAdd, long timeout) {
        addTheList(toAdd, timeout);
    }

    public final List<E> take(int n) {
        if (n == 0) {
            return new LinkedList<>();
        }
        return getTheList(n, -1);
    }

    public final List<E> take(int n, long timeout) {
        if (n == 0) {
            return new LinkedList<>();
        }
        return getTheList(n, timeout);
    }
}
