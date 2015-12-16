package ru.mipt.diht.students.andreyzharkov.threads;

/**
 * Created by Андрей on 15.12.2015.
 */
public class ThreadCounter {
    private final int childrenCount;
    private Integer currentThread;

    class Child extends Thread {
        private int number;

        Child(int num) {
            number = num;
        }

        @Override
        public final void run() {
            while (true) {
                synchronized (currentThread) {
                    if (currentThread.equals(number)) {
                        System.out.println("Thread-" + number);
                        currentThread++;
                        if (currentThread.equals(childrenCount + 1)) {
                            currentThread = 1;
                        }
                    }
                }
            }
        }
    }

    ThreadCounter(int count) {
        childrenCount = count;
        currentThread = 1;
        for (int i = 1; i <= count; i++) {
            Thread child = new Child(i);
            child.start();
        }
    }

    public static void main(String[] args) {
        new ThreadCounter(Integer.valueOf(args[0]));
    }
}
