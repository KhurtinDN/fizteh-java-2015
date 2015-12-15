package ru.mipt.diht.students.simon23rus.Threads;


public class Counter extends Thread {
    private int numberOfThreads;
    private static volatile int currentThread = 0;
    private int myThreadNumber;
    private static Object footLocker = new Object();

    public Counter(int numberOfThreads, int myThreadNumber) {
        this.numberOfThreads = numberOfThreads;
        this.myThreadNumber = myThreadNumber;
    }

    public synchronized void setNextThread() {
        this.currentThread = (this.currentThread + 1) % numberOfThreads;
    }

    public void run() {
        while (true) {
            synchronized (footLocker) {
                if (currentThread == myThreadNumber) {
                    System.out.println("Thread-" + (myThreadNumber + 1));
                    setNextThread();
                    footLocker.notifyAll();
                }
                try {
                    footLocker.wait();
                } catch (InterruptedException exception) {
                }
            }

        }
    }


    public static void callCounter(int threadsNumber) {
        for(int i = 0; i < threadsNumber; ++i) {
            new Counter(threadsNumber, i).start();
        }
    }

    public static void main(String[] args) {

        int threadsNumber = Integer.parseInt(args[1]);
            callCounter(threadsNumber);

    }


}