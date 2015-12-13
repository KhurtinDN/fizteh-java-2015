package ru.mipt.diht.students.pitovsky.threads;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class RollcallThreads implements Runnable {
    private Integer answeredCount;
    private Semaphore askSem;
    private Boolean rollcallEnded;
    private int readyCount;
    private int threadCount;
    private Answerer[] answerers;

    private static final int READY_PROBABILITY = 10;

    public class Answerer extends Thread {
        private Random rand = new Random();
        private Semaphore canAnswerSem = new Semaphore(1);

        @Override
        public final void run() {
            while (true) {
                try {
                    canAnswerSem.acquire();
                    synchronized (rollcallEnded) {
                        if (rollcallEnded) {
                            return;
                        }
                    }
                    boolean ready = true;
                    if (rand.nextInt(READY_PROBABILITY) == 0) {
                        ready = false;
                    }
                    if (ready) {
                        System.out.println(getName() + ": Yes");
                    } else {
                        System.out.println(getName() + ": No");
                    }

                    synchronized (answeredCount) {
                        ++answeredCount;
                        if (ready) {
                            ++readyCount;
                        }
                    }
                    askSem.release();
                } catch (InterruptedException e) {
                    System.err.println(getName() + ": i was interrupted!");
                }
            }
        }
    }

    private void releaseAll() {
        for (int i = 0; i < threadCount; ++i) {
            answerers[i].canAnswerSem.release();
        }
    }

    public RollcallThreads(int newThreadCount) {
        threadCount = newThreadCount;
        answeredCount = 0;
        rollcallEnded = false;
        askSem = new Semaphore(threadCount);
        try {
            askSem.acquire(threadCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        answerers = new Answerer[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            answerers[i] = new Answerer();
            try {
                answerers[i].canAnswerSem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    @Override
    public final void run() {
        boolean allReady = false;
        for (int i = 0; i < threadCount; ++i) {
            answerers[i].start();
        }
        while (!allReady) {
            System.out.println("Are you ready?");
            releaseAll();
            try {
                askSem.acquire(threadCount);
                synchronized (answeredCount) {
                    //System.out.println("ready: " + readyCount);
                    if (readyCount == threadCount) {
                        allReady = true;
                        rollcallEnded = true;
                        releaseAll();
                    }
                    answeredCount = 0;
                    readyCount = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new RollcallThreads(Integer.valueOf(args[0])).run();
    }
}
