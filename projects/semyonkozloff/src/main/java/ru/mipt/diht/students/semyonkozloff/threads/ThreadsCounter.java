package ru.mipt.diht.students.semyonkozloff.threads;

public class ThreadsCounter {

    private final Object monitor = new Object();

    private final int nThreads;
    private volatile int activeThreadId = 0;

    public ThreadsCounter(int initialNthreads) {
        this.nThreads = initialNthreads;
    }

    public final void countThreads() {

        class CountedThread extends Thread {
            private final int threadId;

            CountedThread(int initialId) {
                this.threadId = initialId;
            }

            @Override
            public final void run() {
                while (true) {
                    synchronized (monitor) {
                        if (threadId == activeThreadId) {
                            System.out.println("Thread-" + (threadId + 1));
                            ++activeThreadId;
                            activeThreadId %= nThreads;
                            monitor.notifyAll();
                        } else {
                            try {
                                monitor.wait();
                            } catch (InterruptedException exception) {
                                System.err.println("Thread was interrupted");
                                exception.printStackTrace();
                                System.exit(1);
                            }

                        }
                    }
                }
            }
        }

        for (int i = 0; i < nThreads; ++i) {
            CountedThread countedThread = new CountedThread(i);
            countedThread.start();
        }
    }

    public static void main(String[] args) {
        final int nThreads = 16;
        ThreadsCounter threadsCounter = new ThreadsCounter(nThreads);
        threadsCounter.countThreads();
    }
}
