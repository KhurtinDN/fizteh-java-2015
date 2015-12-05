package ru.mipt.diht.students.ale3otik.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alex on 05.12.15.
 */
public class AreYouReadyInterwiewer {
    private static Object synchronizer = new Object();
    private static volatile int summ;
    private static volatile int answeredCount;
    private static volatile int numOfThreads;
    private static volatile boolean isAwaitForAnswer;
    private static volatile boolean threadsReady;

    private static class AskedThread extends Thread {
        private Random randomEngine = new Random();

        @Override
        @SuppressWarnings("checkstyle:magicnumber")
        public void run() {
            try {
                while (true) {
                    synchronized (synchronizer) {
                        while (!isAwaitForAnswer) {
                            synchronizer.wait();
                        }

                        if (randomEngine.nextInt() % 10 < 9) {
                            System.out.println("Yes");
                            ++summ;
                        } else {
                            System.out.println("No");
                        }
                        ++answeredCount;
                        if (answeredCount == numOfThreads) {
                            threadsReady = true;
                            isAwaitForAnswer = false;
                            synchronizer.notifyAll();
                        }
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        numOfThreads = new Integer(args[0]);
        isAwaitForAnswer = false;
        List<AskedThread> threads = new ArrayList<>();
        for (int i = 0; i < numOfThreads; ++i) {
            AskedThread asked = new AskedThread();
            threads.add(asked);
            asked.start();
        }

        try {
            while (true) {
                answeredCount = 0;
                summ = 0;
                System.out.println("Are you ready?");

                synchronized (synchronizer) {
                    threadsReady = false;
                    isAwaitForAnswer = true;
                    synchronizer.notifyAll();
                    while (!threadsReady) {
                        synchronizer.wait();
                    }
                    if (summ == numOfThreads) {
                        break;
                    }
                }
            }
            for (AskedThread curThread : threads) {
                curThread.interrupt();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

