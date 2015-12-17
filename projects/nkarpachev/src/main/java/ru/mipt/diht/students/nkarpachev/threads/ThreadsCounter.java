package ru.mipt.diht.students.nkarpachev.threads;

public class ThreadsCounter {
    private static Object monitor = new Object();
    private static volatile int activeThread = 0;

    public static class CounterRunner extends Thread {

        private int threadID, totalThreadsNumber;

        CounterRunner(int threadNumber, int totalThreads) {
            threadID = threadNumber;
            totalThreadsNumber = totalThreads;
        }

        @Override
        public final void run() {
            try {
                while (true) {
                    synchronized (monitor) {
                        if (threadID == activeThread) {
                            System.out.println("Thread-" + (threadID + 1));
                            activeThread++;
                            activeThread %= totalThreadsNumber;
                            monitor.notifyAll();
                        } else {
                            monitor.wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("We were interrupted");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        int totalThreads = 0;
        try {
            if (args.length != 1) {
                throw new Exception();
            }
            totalThreads = Integer.parseInt(args[0]);
            if (totalThreads <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Wrong input arguments");
            System.exit(1);
        }
        for (int i = 0; i < totalThreads; i++) {
            CounterRunner thread = new CounterRunner(i, totalThreads);
            thread.start();
        }
    }
}
