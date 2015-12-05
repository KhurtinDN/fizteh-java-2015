package ru.mipt.diht.students.feezboom.Threads;

import java.util.Objects;
import java.util.Scanner;

/**
 * * Created by avk on 06.12.15.
 **/
public class Threads {
    @SuppressWarnings("checkstyle:designforextension")
    public static class OurThreadTask implements Runnable {
        private int id, n;
        private static int activeThread = 0;
        private static Object lock = new Object();

        @SuppressWarnings("checkstyle:hiddenfield")
        public OurThreadTask(int id, int n) {
            this.id  = id; this.n = n;
        }

        @Override
        public synchronized void run() {
            synchronized (lock) {
                for (;;) {
                    while (activeThread != id)
                        try {
                            lock.wait();
                        } catch (InterruptedException ex) {
                            System.out.println("Thread-" + id + "was interrupted.");
                        }
                    System.out.println("Thread-" + id);
                    activeThread = (activeThread + 1) % n;
                    lock.notify();
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello, Threads!");

        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();



        for (int i = 0; i < n; ++i) {
            OurThreadTask ourThreadTask = new OurThreadTask(i + 1, n);
            new Thread(ourThreadTask).start();

        }

    }
}
