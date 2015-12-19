package ru.mipt.diht.students.elinrin.threads;

import ru.mipt.diht.students.elinrin.threads.exception.HandlerOfException;


public class Counter {

    private static volatile int printId;

    public static void main(final String[] args) {
        int number;
        number = Parse.parse(args);

        for (int id = 0; id < number; id++) {
            CounterThread thread = new CounterThread(id, (id + 1) % number);
            thread.start();
        }
    }


    private static Object working = new Object();

    private static class CounterThread extends Thread {
        private int threadId, nextThreadId;

        CounterThread(final int id1, final int id2) {
            threadId = id1;
            nextThreadId = id2;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (working) {
                    while (threadId != printId) {
                        try {
                            working.wait();
                        } catch (InterruptedException e) {
                            HandlerOfException.handler(e);
                        }
                    }
                    System.out.println("Thread-" + (threadId + 1));
                    printId = nextThreadId;
                    working.notifyAll();
                }
            }
        }
    }
}
