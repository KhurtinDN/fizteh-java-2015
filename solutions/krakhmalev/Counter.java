package ru.fizteh.fivt.students.krakhmalev.Threads;



public class Counter {

    private static volatile int currentID;

    private static Object synchronizer = new Object();

    private static class OneCounter implements Runnable {
        private int id, size;


        OneCounter(int id, int size) {
            this.id = id;
            this.size = size;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (synchronizer) {
                        while (id != currentID) {
                            synchronizer.wait();
                        }
                        System.out.println("Thread-" + String.valueOf(id));
                        currentID++;
                        if (currentID > size) {
                            currentID %= size;
                        }
                        synchronizer.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IllegalArgumentException {
        if (args.length != 1) {
            throw new IllegalArgumentException();
        }
        int n;
        n = Integer.valueOf(args[0]);
        currentID = 1;
        for (int i = 0; i < n; i++) {
            Thread oneCounter = new Thread(new OneCounter(i + 1, n));
            oneCounter.start();
        }
    }
}

