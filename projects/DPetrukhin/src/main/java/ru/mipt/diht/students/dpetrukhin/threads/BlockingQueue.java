package ru.mipt.diht.students.dpetrukhin.threads;

import java.util.*;

/**
 * Created by daniel on 19.12.15.
 */

public class BlockingQueue<T> {
    private Object queueAccessSyncObj = new Object();
    private Object actionSyncObj = new Object();
    private int maxQueueSize;
    private int queueSize;
    private volatile long currentOfferCounter;
    private volatile long currentTakeCounter;
    private long offerCounter;
    private long takeCounter;
    private List<T> queue;

    public BlockingQueue(final int newMaxQueueSize) {
        maxQueueSize = newMaxQueueSize;
        currentOfferCounter = 0;
        currentTakeCounter = 0;
        offerCounter = 0;
        takeCounter = 0;
        queueSize = 0;
        queue = new LinkedList<>();
    }

    public final void offer(final List<T> list) {
        offer(list, 0);
    }

    public final void offer(final List<T> list, final long timeout) {
        final boolean existTimeLimit;
        if (timeout > 0) {
            existTimeLimit = true;
        } else {
            existTimeLimit = false;
        }
        long timeToStop = System.currentTimeMillis() + timeout;
        long timeToSleep;

        if (list.size() > maxQueueSize) {
            return;
        }
        long orderNumber;

        synchronized (actionSyncObj) {
            orderNumber = offerCounter++;
            if (offerCounter == Long.MAX_VALUE) {
                offerCounter = 0;
            }
        }

        try {
            synchronized (actionSyncObj) {
                while (true) {
                    if (currentOfferCounter == orderNumber) {
                        if (list.size() + queueSize <= maxQueueSize) {
                            synchronized (actionSyncObj) {
                                queue.addAll(list);
                                queueSize += list.size();
                            }

                            ++currentOfferCounter;
                            actionSyncObj.notifyAll();
                            throw new InterruptedException("");
                        }
                    }
                    actionSyncObj.notifyAll();
                    if (existTimeLimit) {
                        timeToSleep = timeToStop - System.currentTimeMillis();
                        if (timeToSleep <= 0) {
                            ++currentOfferCounter;
                            actionSyncObj.notifyAll();
                            throw new InterruptedException("");
                        }
                        actionSyncObj.wait(timeToSleep);
                    } else {
                        actionSyncObj.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public final List take(final int qnt) {
        return take(qnt, 0);
    }

    public final List take(final int qnt, final long timeout) {
        final boolean existTimeLimit;
        if (timeout > 0) {
            existTimeLimit = true;
        } else {
            existTimeLimit = false;
        }
        long timeToStop = System.currentTimeMillis() + timeout;
        long timeToSleep;

        if (qnt == 0) {
            return new LinkedList<>();
        }

        if (qnt > maxQueueSize) {
            return null;
        }

        long orderNumber;

        synchronized (actionSyncObj) {
            orderNumber = takeCounter++;
            if (takeCounter == Long.MAX_VALUE) {
                takeCounter = 0;
            }
        }

        try {
            synchronized (actionSyncObj) {
                while (true) {
                    if (currentTakeCounter == orderNumber) {
                        if (qnt <= queueSize) {
                            List answer;
                            synchronized (actionSyncObj) {
                                answer = new LinkedList<>(queue.subList(0, qnt));
                                queue.subList(0, qnt).clear();
                                queueSize -= qnt;
                            }

                            ++currentTakeCounter;
                            actionSyncObj.notifyAll();
                            return answer;
                        }
                    }
                    actionSyncObj.notifyAll();
                    if (existTimeLimit) {
                        timeToSleep = timeToStop - System.currentTimeMillis();
                        if (timeToSleep <= 0) {
                            ++currentTakeCounter;
                            actionSyncObj.notifyAll();
                            throw new InterruptedException("");
                        }
                        actionSyncObj.wait(timeToSleep);
                    } else {
                        actionSyncObj.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            return null;
        }
    }
}
