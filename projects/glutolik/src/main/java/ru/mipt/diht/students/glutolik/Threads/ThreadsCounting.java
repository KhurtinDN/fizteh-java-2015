package ru.mipt.diht.students.glutolik.Threads;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by glutolik on 14.12.15.
 */
public class ThreadsCounting {
    private static volatile Integer numberOfThreads;
    private List<Thread> kinderGarten = new ArrayList<>();
    private volatile Integer current = 1;

    public static Integer getNumber() {
        return numberOfThreads;
    }

    private class Child extends Thread {
        private int id;


        Child(int number) {
            id = number;
        }

        @Override
        public String toString() {
            return "Thread-" + id;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                synchronized (current) {
                    if (current == id) {
                        System.out.println(this);
                        current++;
                        Thread.currentThread().interrupt();
                        if (current > numberOfThreads) {
                            current = 0;
                        }
                    }
                }
            }
        }
    }

    public ThreadsCounting(int number) {
        numberOfThreads = number;
        for (int i = 0; i < numberOfThreads; i++) {
            Thread child = new Child(i + 1);
            child.start();
            kinderGarten.add(child);
        }
    }

    public static void main(String[] args) {
        new ThreadsCounting(Integer.valueOf(args[0]));
    }

}
