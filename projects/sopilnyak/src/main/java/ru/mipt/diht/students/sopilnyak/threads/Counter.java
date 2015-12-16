package ru.mipt.diht.students.sopilnyak.threads;

public class Counter {

    private static final Object MONITOR = new Object();

    private static int currentId;

    private static class ThreadCount extends Thread {

        private int id, nextId;

        ThreadCount(int inputId, int inputNextId) {
            this.id = inputId;
            this.nextId = inputNextId;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (MONITOR) {
                    while (id != currentId) {
                        try {
                            MONITOR.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Thread-" + String.valueOf(id + 1));
                    currentId = nextId;
                    MONITOR.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        int n;
        try {
            n = Integer.valueOf(args[0]);
            if (n <= 0) {
                throw new NumberFormatException();
            }
        } catch (Exception e) {
            System.err.println("Wrong number of threads");
            return;
        }
        currentId = 0;
        for (int i = 0; i < n; i++) {
            ThreadCount thread = new ThreadCount(i, (i + 1) % n);
            thread.start();
        }
    }
}
