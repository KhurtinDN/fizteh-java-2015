package ru.mipt.diht.students.annnvl.Threads.Counter;

public class CountedThread extends Thread {
    private static int numberOfThreads;
    private static volatile int currentThread = 0;
    private final int myNumber;

    CountedThread(int mynum) {
        myNumber = mynum;
    }

    public static void setNumberOfThreads(int num) {
        numberOfThreads = num;
    }

    @Override
    public void run() {
        while (true) {
            while (myNumber != currentThread) {
                Thread.yield();
            }
            System.out.println("Thread-" + (myNumber + 1) + "\n");
            currentThread = (currentThread + 1) % numberOfThreads;
        }
    }
}
