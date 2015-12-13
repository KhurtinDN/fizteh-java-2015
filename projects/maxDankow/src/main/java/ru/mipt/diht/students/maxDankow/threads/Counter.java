package ru.mipt.diht.students.maxDankow.threads;

import java.util.concurrent.*;

public class Counter {
    public static volatile Integer count = 0;

    public void counting(int unitNumber) {
        count = 0;
        ExecutorService exec = Executors.newCachedThreadPool();
        CyclicBarrier barrier = new CyclicBarrier(unitNumber, () -> {
            synchronized (count) {
                if (count < unitNumber) {
                    ++count;
                } else {
                    exec.shutdownNow();
                }
            }
        });
        for (int id = 1; id <= unitNumber; ++id) {
            exec.execute(new CountingUnit(id, barrier));
        }
        waitForAll(exec);
    }

    // Ожидает завершения всех потоков в исполнителе.
    public void waitForAll(ExecutorService exec) {
        exec.shutdown();
        try {
            while (!exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS)) ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class CountingUnit implements Runnable {
        int id;
        CyclicBarrier barrier;

        public CountingUnit(int id, CyclicBarrier barrier) {
            this.id = id;
            this.barrier = barrier;
        }

        @Override
        public String toString() {
            return "Thread-" + id;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (Counter.count) {
                        if (count == id) {
                            System.out.println(this);
                        }
                    }
                    barrier.await();
                }
            } catch (InterruptedException ignored) {
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
