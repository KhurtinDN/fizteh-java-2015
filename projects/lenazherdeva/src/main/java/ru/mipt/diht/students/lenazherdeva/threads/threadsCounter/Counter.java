package ru.mipt.diht.students.lenazherdeva.threads.threadsCounter;

/**
 * Created by admin on 13.12.2015.
 */
public class Counter extends Thread {

    private static int numberOfThreads;
    private int numberOfThisThread;
    private static Object object = new Object();
    private static volatile int currentThread = 1;

    Counter(int numberOfTread, int number) {
        numberOfThisThread = numberOfTread;
        numberOfThreads = number;
    }

    @Override
    public final void run() {
        synchronized (object) {
            while (true) {
                if (currentThread == numberOfThisThread) {
                    System.out.println("Thread-" + currentThread);
                    currentThread %= numberOfThreads;
                    currentThread++;
                    object.notifyAll();
                }
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
