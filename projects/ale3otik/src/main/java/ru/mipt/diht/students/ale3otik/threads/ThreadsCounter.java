package ru.mipt.diht.students.ale3otik.threads;


/**
 * Created by alex on 05.12.15.
 */

public class ThreadsCounter {
    private static class Counter {
        private static final long SLEEP_TIME = 1000;
        private static Object synchronizer = new Object();
        private static volatile int currentNum;
        private static volatile int numOfThreads;

        private class InnerThreadCounter extends Thread {
            private int myNumber;

            InnerThreadCounter(int num) {
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

        public void count(String[] args) {
            numOfThreads = new Integer(args[0]);
            currentNum = 0;
            for (int i = 0; i < numOfThreads; ++i) {
                InnerThreadCounter counter = new InnerThreadCounter(i);
                counter.start();
            }
        }
    }

    public static void main(String[] args) {
        Counter counter = new Counter();
        counter.count(args);
    }
}
