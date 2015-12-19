package ru.mipt.diht.students.IrinaMudrova.Threads;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RollCall {
    private static Integer quantity;
    private static Integer currentIteration;
    private static Boolean currentAnswer;
    private static Lock lock = new ReentrantLock(true);
    private static Condition needWrite = lock.newCondition();
    private static CountDownLatch countDownLatch;
    private static Thread[] threads;

    public static void main(String[] args) throws Exception {

        class OurTask implements Runnable {
            private final Integer id;

            OurTask(Integer id) {
                this.id = id;
            }

            @Override
            public void run() {
                Integer iteration = 0;
                Random rand = new Random();
                while (true) {
                    lock.lock();
                    try {
                        while (!currentIteration.equals(iteration)) {
                            needWrite.await();
                            if (Thread.interrupted()) {
                              //  System.err.println("Interrupting thread");
                                return;
                            }
                        }
                        final int num = 10;
                        if (rand.nextInt() % num == 0) {
                            synchronized (System.out) {
                                System.out.print("No ");
                                currentAnswer = false;
                            }
                        } else {
                            synchronized (System.out) {
                                System.out.print("Yes ");
                            }
                        }
                        countDownLatch.countDown();
                        ++iteration;
                    } catch (InterruptedException e) {
                        //System.err.println("Interrupted error" );
                        return;
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }

        quantity = Integer.valueOf(args[0]);
        threads = new Thread[quantity];
        for (int i = 0; i < quantity; i++) {
            threads[i] = new Thread(new OurTask(i));
        }
        currentIteration = -1;
        for (Thread thread : threads) {
            thread.start();
        }
        while (true) {
            synchronized (System.out) {
                System.out.println("Are you ready?");
            }
            lock.lock();
            try {
                countDownLatch = new CountDownLatch(quantity);
                currentAnswer = true;
                currentIteration++;
                needWrite.signalAll();
            } finally {
                lock.unlock();
            }
            countDownLatch.await();
            synchronized (System.out) {
                System.out.println("");
            }
            if (currentAnswer) {
                break;
            }
        }
        System.out.println("All threads printed 'Yes'!");
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
