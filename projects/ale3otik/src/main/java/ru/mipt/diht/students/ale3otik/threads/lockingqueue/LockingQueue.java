package ru.mipt.diht.students.ale3otik.threads.lockingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by alex on 06.12.15.
 */
public class LockingQueue<E> {
    private final Object synchronizer = new Object();
    private volatile long currentOfferNumber;
    private volatile long currentTakeNumber;
    private long nextOfferNumber;
    private long nextTakeNumber;
    private volatile int maxQueueSize;
    private volatile boolean queueIsUsingNow;
    private List<E> queue;

    private class OfferThread<E> extends Thread {
        private final long myActNum;
        private final List<E> toAdd;
        private final CountDownLatch latch;

        OfferThread(long myActionNumber, List<E> listToAdd, CountDownLatch rcvLatch) {
            this.myActNum = myActionNumber;
            this.toAdd = listToAdd;
            this.latch = rcvLatch;
        }

        @Override
        public void run() {
            try {
                synchronized (synchronizer) {
                    while (this.myActNum != currentOfferNumber
                            || queue.size() + toAdd.size() > maxQueueSize) {
                        synchronizer.wait();
                    }
                    queueIsUsingNow = true;

                    queue.addAll(new ArrayList(toAdd));
                    ++currentOfferNumber;

                    queueIsUsingNow = false;
                    synchronizer.notifyAll();
                    latch.countDown();
                }
            } catch (InterruptedException e) {
                ++currentOfferNumber;
                return;
            }
        }
    }

    private class TakeThread<E> extends Thread {
        private final long myActNum;
        private final int lengthToTake;
        private List<E> answer;
        private final CountDownLatch latch;

        TakeThread(long myActionNumber, int lenToTake, final CountDownLatch rcvLatch) {
            this.myActNum = myActionNumber;
            this.lengthToTake = lenToTake;
            this.latch = rcvLatch;
        }

        public List<E> getTheList() {
            return answer;
        }

        @Override
        public void run() {
            try {
                synchronized (synchronizer) {
                    while (this.myActNum != currentTakeNumber
                            || lengthToTake > queue.size()) {
                        synchronizer.wait();
                    }

                    queueIsUsingNow = true;
                    answer = (List) queue.subList(0, lengthToTake);
                    queue = queue.subList(lengthToTake, queue.size());
                    ++currentTakeNumber;

                    queueIsUsingNow = false;
                    synchronizer.notifyAll();

                    latch.countDown();
                }
            } catch (InterruptedException e) {
                latch.countDown();
                ++currentTakeNumber;
                return;
            }
        }
    }


    public LockingQueue(int maxSize) {
        queue = new ArrayList<>();
        this.maxQueueSize = maxSize;
        currentOfferNumber = 0;
        currentTakeNumber = 0;
        nextOfferNumber = 0;
        nextTakeNumber = 0;
        queueIsUsingNow = false;
    }

    public final void offer(List<E> toAdd) {

        final CountDownLatch latch = new CountDownLatch(1);
        OfferThread<E> thread = new OfferThread<>(nextOfferNumber, toAdd, latch);

        ++nextOfferNumber;
        if (nextOfferNumber == Long.MAX_VALUE) {
            nextOfferNumber = 0;
        }

        thread.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            thread.interrupt();
            return;
        }


    }

    public final void offer(List e, long timeout) {

    }

    public final List<E> take(int n) {
        if (n == 0) {
            return new ArrayList<>();
        }

        final CountDownLatch latch = new CountDownLatch(1);
        TakeThread<E> thread = new TakeThread<>(nextTakeNumber, n, latch);

        ++nextTakeNumber;
        if (nextTakeNumber == Long.MAX_VALUE) {
            nextTakeNumber = 0;
        }
        System.out.println("start");
        thread.start();
        System.out.println("finale");

        try {
            latch.await();
        } catch (InterruptedException e) {
            thread.interrupt();
            return null;
        }

        return thread.getTheList();
    }

//    public void List take(int n, long timeout) {
//        return new ArrayList<T>();
//    }
}
