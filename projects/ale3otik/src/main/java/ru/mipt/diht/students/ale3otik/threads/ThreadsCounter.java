package ru.mipt.diht.students.ale3otik.threads;

/**
 * Created by alex on 05.12.15.
 */

public class ThreadsCounter {
    private static final long SLEEP_TIME = 1000;
    private static Object synchronizer = new Object();
    private static volatile int currentNum;
    private static volatile int numOfThreads;

    private static class Counter extends Thread {
        private int myNumber;

        Counter(int num) {
            this.myNumber = num;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (synchronizer) {
                        while (currentNum != this.myNumber) {
                            synchronizer.wait();
                        }

                        System.out.println("Thread-" + (this.myNumber + 1));
                        ++currentNum;
                        currentNum %= numOfThreads;
                        if (this.myNumber == numOfThreads - 1) {
                            Thread.sleep(SLEEP_TIME);
                        }
                        synchronizer.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        numOfThreads = new Integer(args[0]);
        currentNum = 0;
        for (int i = 0; i < numOfThreads; ++i) {
            Counter counter = new Counter(i);
            counter.start();
        }
    }
}
