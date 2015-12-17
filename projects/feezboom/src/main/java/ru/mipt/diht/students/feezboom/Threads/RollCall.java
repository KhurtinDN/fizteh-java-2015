package ru.mipt.diht.students.feezboom.Threads;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * * Created by avk on 17.12.15.
 **/

public class RollCall {

    private final Random random = new Random();
    private ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);
    private final Object lock = new Object();
    private volatile boolean ready = true;
    private static final int THREADS_NUMBER = 10;
    private CyclicBarrier queue;


    public final void performRollCall() {
        System.out.println("Are you ready, kids?");
        queue = new CyclicBarrier(THREADS_NUMBER, new FatherTask());
        for (int i = 0; i < THREADS_NUMBER; i++) {
            executorService.execute(new ChildTask());
        }
    }

    private class FatherTask implements Runnable {
        @Override
        public void run() {
            if (!ready) {
                synchronized (lock) {
                    ready = true;
                    System.out.println("again");
                }
            } else {
                System.out.println("Who lives in a pine-apple under the sea?");
                System.out.println("SPONGE BOB SQUARE PANTS!");
                executorService.shutdownNow();
            }
        }
    }

    private class ChildTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                final int edge = 10;
                final int max = 100;
                if (random.nextInt(max) > edge) {
                    System.out.println("AYE AYE CAPTAIN!");
                } else {
                    System.out.println("NO");
                    synchronized (lock) {
                        ready = false;
                    }
                }
                try {
                    queue.await();
                } catch (InterruptedException i) {
                    System.out.println("Na pravah reklamy");
                } catch (BrokenBarrierException i) {
                    i.printStackTrace();
                }
            }
        }
    }
}
