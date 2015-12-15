package ru.mipt.diht.students.egdeliya.Thread;

//import java.util.Objects;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Эгделия on 15.12.2015.
 */
public class Counter {
    private static Object object = new Object();
    private static int numberOfThreads;
    private static int currentThreadId;
    private static Thread[] threads;

    public static void main(String[] args) {
        numberOfThreads = Integer.parseInt(args[0]);
        threadsCounter();
    }

    public static void threadsCounter() {
        threads = new Thread[numberOfThreads];
        currentThreadId = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new PrintName(i, (i + 1) % numberOfThreads));
            threads[i].run();
        }
    }

    public static class PrintName implements Runnable {
        private int myThreadId, nextThreadId;

        public PrintName(int id1, int id2) {
            myThreadId = id1;
            nextThreadId = id2;
        }

        @Override
        public final synchronized void run() {
            while (true) {
                synchronized (object) {
                    while (myThreadId != currentThreadId) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            System.err.println("Thread " + myThreadId + " interrupted");
                        }
                    }
                    System.out.println("Thread-" + myThreadId);
                    currentThreadId = nextThreadId;
                    object.notifyAll();
                    break;
                }
            }
        }
    }
}

