package ru.mipt.diht.students.egdeliya.Thread;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Эгделия on 17.12.2015.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class Call {
    private Thread[] threads;
    private int numberOfThreads;
    private Random randomAnswer = new Random();
    private int threadAnswer;
    private final int percentage = 10;
    private boolean allThreadsReplyYes = false;
    private Lock lock = new ReentrantLock();

    public final void threadsCall(String arg) {
        numberOfThreads = Integer.parseInt(arg);
        threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i) {
            threads[i] = new Thread(new Reply());
        }

        while (!allThreadsReplyYes) {
            System.out.println("Are you ready?");
            allThreadsReplyYes = true;
            for (int i = 0; i < numberOfThreads; ++i) {
                threads[i].run();
            }
        }
    }

    public class Reply implements Runnable {

        public final void run() {
            lock.lock();
            threadAnswer = randomAnswer.nextInt(percentage);
            if (threadAnswer == 1) {
                System.out.println("No");
                allThreadsReplyYes = false;
            } else {
                System.out.println("Yes");
            }
            lock.unlock();
        }
    }
}
