package ru.mipt.diht.students.egdeliya.Thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Эгделия on 15.12.2015.
 */
public class Counter {
    private static int numberOfThreads;
    private static Lock lock = new ReentrantLock(true);

    public static void main(String[] args) {
        numberOfThreads = Integer.parseInt(args[0]);
        threadsCounter();
    }

    public static void threadsCounter() {
        for (int i = 0; i < numberOfThreads; i++) {
            Thread t = new Thread();
            lock.lock();
            try {
                System.out.println(t.getName());
            } finally {
                lock.unlock();
            }
        }
    }
}
