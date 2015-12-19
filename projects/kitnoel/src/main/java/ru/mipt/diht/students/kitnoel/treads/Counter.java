package ru.mipt.diht.students.kitnoel.treads;

/**
 * Created by leonk on 19.12.15.
 */
public class Counter {

    private static volatile int currentID;

    private static Object synch = new Object();

    private static class Runner implements Runnable {
        private int id, size;


        Runner(int id, int size) {
            this.id = id;
            this.size = size;
        }

        @Override
        public void run() {
            while (true) try {
                synchronized (synch) {
                    while (id != currentID) synch.wait();
                    System.out.println("Thread-" + String.valueOf(id));
                    currentID++;
                    if (currentID > size) currentID %= size;
                    synch.notifyAll();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int n;
        n = Integer.valueOf(args[0]);
        currentID = 1;
        for (int i = 0; i < n; i++) {
            Thread Runner = new Thread(new Runner(i + 1, n));
            Runner.start();
        }
    }
}
