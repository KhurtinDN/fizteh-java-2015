package ru.mipt.diht.students.ale3otik.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alex on 05.12.15.
 */
public class AreYouReadyInterviewer {
    private static class Interviewer {
        private Object synchronizer = new Object();
        private volatile int summ;
        private volatile int answeredCount;
        private volatile int numOfThreads;
        private volatile boolean isAwaitForAnswer;
        private volatile boolean threadsReady;

        private class AskedThread extends Thread {
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

                            if (randomEngine.nextInt(10) < 9) {
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

        public void ask(String[] args) {
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

    public static void main(String[] args) {
        assert args.length > 0;
        Interviewer interviewer = new Interviewer();
        interviewer.ask(args);
    }
}
