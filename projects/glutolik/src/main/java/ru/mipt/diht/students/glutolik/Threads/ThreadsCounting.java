package ru.mipt.diht.students.glutolik.Threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by glutolik on 14.12.15.
 */
public class ThreadsCounting {
    private static volatile Integer numberOfThreads;
    private List<Thread> kinderGarten = new ArrayList<>();

    public static Integer getNumber() {
        return numberOfThreads;
    }

    private class Child extends Thread {
        private int id;
        private CyclicBarrier barrier;

        Child(int number, CyclicBarrier barrier1) {
            id = number;
            barrier = barrier1;
        }

        @Override
        public String toString() {
            return "Thread-" + id;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (numberOfThreads) {
                        if (numberOfThreads == id) {
                            System.out.println(this);
                        }
                    }
                    barrier.await();
                }
            } catch (InterruptedException exception) {
            } catch (BrokenBarrierException exception1) {
                exception1.printStackTrace();
            }
        }
    }

    public ThreadsCounting(int number) {
        numberOfThreads = 0;
        CyclicBarrier barrier1 = new CyclicBarrier(number, () -> {
            synchronized (numberOfThreads) {
                if (numberOfThreads < number) {
                    ++numberOfThreads;
                } else {
                    for (Thread ch : kinderGarten) {
                        ch.interrupt();
                    }
                }
            }
        });
        for (int i = 0; i < number; i++) {
            Thread child = new Child(i + 1, barrier1);
            child.start();
            kinderGarten.add(child);
        }
    }

    public static void main(String[] args) {
        new ThreadsCounting(Integer.valueOf(args[0]));
    }

}
