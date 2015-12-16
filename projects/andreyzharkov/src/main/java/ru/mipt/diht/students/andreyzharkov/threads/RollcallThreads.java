package ru.mipt.diht.students.andreyzharkov.threads;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by Андрей on 15.12.2015.
 */
public class RollcallThreads implements Runnable {
    private Integer answersCount;
    private Semaphore sem;
    private Boolean isRollcallFinished;
    private int correctAnswers;
    private int childrenCount;
    private Child[] children;

    private static final int FAIL_PROBABILITY = 10;

    public class Child extends Thread {
        private Random rand = new Random();
        private Semaphore canAnswerSem = new Semaphore(1);

        @Override
        public final void run() {
            while (true) {
                try {
                    canAnswerSem.acquire();
                    synchronized (isRollcallFinished) {
                        if (isRollcallFinished) {
                            return;
                        }
                    }
                    boolean ready = (rand.nextInt(FAIL_PROBABILITY) != 0);
                    if (ready) {
                        System.out.println(getName() + ": Yes");
                    } else {
                        System.out.println(getName() + ": No");
                    }

                    synchronized (answersCount) {
                        answersCount++;
                        if (ready) {
                            correctAnswers++;
                        }
                    }
                    sem.release();
                } catch (InterruptedException e) {
                    System.err.println(getName() + ": i was interrupted!");
                }
            }
        }
    }

    private void releaseAll() {
        for (int i = 0; i < childrenCount; ++i) {
            children[i].canAnswerSem.release();
        }
    }

    public RollcallThreads(int count) {
        childrenCount = count;
        answersCount = 0;
        isRollcallFinished = false;
        sem = new Semaphore(childrenCount);
        try {
            sem.acquire(childrenCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        children = new Child[childrenCount];
        for (int i = 0; i < childrenCount; ++i) {
            children[i] = new Child();
            try {
                children[i].canAnswerSem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    @Override
    public final void run() {
        boolean allReady = false;
        for (int i = 0; i < childrenCount; ++i) {
            children[i].start();
        }
        while (!allReady) {
            System.out.println("Are you ready?");
            releaseAll();
            try {
                sem.acquire(childrenCount);
                synchronized (answersCount) {
                    if (correctAnswers == childrenCount) {
                        allReady = true;
                        isRollcallFinished = true;
                        releaseAll();
                    }
                    answersCount = 0;
                    correctAnswers = 0;
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
