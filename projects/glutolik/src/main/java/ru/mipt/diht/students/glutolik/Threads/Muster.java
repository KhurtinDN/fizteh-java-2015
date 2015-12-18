package ru.mipt.diht.students.glutolik.Threads;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by glutolik on 14.12.15.
 */
public class Muster {
    private static volatile Boolean ready = true;


    private class MusterChild extends Thread {
        private int id;
        private final Random random = new Random();
        private final int success = 90;
        private final int maximum = 99;
        private CyclicBarrier barrier;

        MusterChild(int number, CyclicBarrier barrier1) {
            id = number;
            barrier = barrier1;
        }

        @Override
        public String toString() {
            return "Thread-" + id;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if (random.nextInt(maximum) > success) {
                    System.out.println(this + " NO");
                    ready = false;
                    //Thread.currentThread().interrupt();
                } else {
                    System.out.println(this + " YES");
                    //Thread.currentThread().interrupt();
                }
                try {
                    barrier.await();
                } catch (InterruptedException ignored) {
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final void muster(int number) {
        System.out.println("Are you ready?");
        ExecutorService exec = Executors.newCachedThreadPool();
        CyclicBarrier barrier = new CyclicBarrier(number, () -> {
            synchronized (ready) {
                if (ready) {
                    System.out.println("Everyone is ready!");
                    exec.shutdownNow();
                } else {
                    synchronized (ready) {
                        ready = true;
                        System.out.println("I'm asking again. Are you ready?");
                    }
                }
            }
        });
        for (int i = 0; i < number; i++) {
            exec.execute(new MusterChild(i + 1, barrier));
        }
    }

}

