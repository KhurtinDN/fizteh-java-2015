package ru.mipt.diht.students.semyonkozloff.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;

public class Rollcall {

    private static final int PROBABILITY_RANGE = 100;
    private static final int FAILURE_PROBABILITY = 10;

    private volatile boolean allThreadsAreReady = true;

    private final int nThreads;
    private Phaser startPhaser;
    private Phaser finishPhaser;

    public Rollcall(int initialNthreads) {
        this.nThreads = initialNthreads;
    }

    public final void startRollCall() {
        startPhaser = new Phaser(nThreads + 1);
        finishPhaser = new Phaser(nThreads + 1);
        List<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < nThreads; ++i) {

            Thread thread = new Thread() {
                private Random randomGenerator = new Random();

                @Override
                public final void run() {
                    while (true) {
                        startPhaser.arriveAndAwaitAdvance();
                        int randomValue =
                                randomGenerator.nextInt(PROBABILITY_RANGE);
                        if (randomValue > FAILURE_PROBABILITY) {
                            System.out.println("Yes");
                            allThreadsAreReady &= true;
                        } else {
                            System.out.println("No");
                            allThreadsAreReady &= false;
                        }
                        finishPhaser.arriveAndAwaitAdvance();
                    }
                }
            };

            threads.add(thread);
            thread.start();
        }

        while (true) {
            allThreadsAreReady = true;
            System.out.println("Are you ready?");
            startPhaser.arriveAndAwaitAdvance();
            // Threads are working here
            finishPhaser.arriveAndAwaitAdvance();
            if (allThreadsAreReady) {
                for (Thread thread : threads) {
                    thread.interrupt();
                }
                return;
            }
        }
    }

    public static void main(String[] args) {
        final int nThreads = 8;
        Rollcall rollcall = new Rollcall(nThreads);
        rollcall.startRollCall();
    }
}
