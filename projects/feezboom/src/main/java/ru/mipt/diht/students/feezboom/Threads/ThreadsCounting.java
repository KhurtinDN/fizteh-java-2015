package ru.mipt.diht.students.feezboom.Threads;

/**
 * * Created by avk on 17.12.15.
 **/
public class ThreadsCounting {
    private Object lock = new Object();
    private int currentNumber = 1;
    private int threadsNumber;

    ThreadsCounting(int numberOfThreadsToStart) {
        threadsNumber = numberOfThreadsToStart;
        for (int i = 0; i < numberOfThreadsToStart; i++) {
            MyThread myThread = new MyThread(i);
            myThread.start();
        }
    }

    class MyThread extends Thread {
        private int id;

        MyThread(int ourId) {
            id = ourId;
        }


        @Override
        public String toString() {
            return "Thread-" + id;
        }

        @Override
        public void run() {
            for (; !Thread.currentThread().isInterrupted();) {
                synchronized (lock) {
                    if (currentNumber == id) {
                        System.out.println(this.toString());
                        currentNumber = (currentNumber + 1) % threadsNumber;
                        if (currentNumber == 0) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException i) {
                                System.out.println("Error : " + i);
                            }
                        }
                    } else {
                        lock.notify();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ThreadsCounting threadsCounting = new ThreadsCounting(10);
    }

}


