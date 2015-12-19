package ru.mipt.diht.students.tveritinova.Threads;

public class Counter {

    private static Object object = new Object();
    private static int cnt;
    private static int curThread;
    private static Thread[] threads;

    public static void main(String[] args) {
        cnt = Integer.parseInt(args[0]);
        threads = new Thread[cnt];
        curThread = 0;

        for (int i = 0; i < cnt; i++) {
            threads[i] = new CounterThread(i, (i + 1) % cnt);
            threads[i].start();
        }
    }

    public static class CounterThread extends Thread {

        private int thisThread, nextThread;

        public CounterThread(int ths, int next) {
            thisThread = ths;
            nextThread = next;
        }

        @Override
        public final synchronized void run() {
            while (true) {
                synchronized (object) {
                    while (thisThread != curThread) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            System.err.println("Thread "
                                    + thisThread + " interrupted");
                        }
                    }
                    System.out.println("Thread-" + thisThread);
                    curThread = nextThread;
                    object.notifyAll();
                }
            }
        }
    }
}
