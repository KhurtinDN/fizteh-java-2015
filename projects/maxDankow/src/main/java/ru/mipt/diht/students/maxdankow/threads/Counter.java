package ru.mipt.diht.students.maxdankow.threads;

import java.util.concurrent.*;

public class Counter {
    private static volatile Integer count = 0;

    public static Integer getCount() {
        return count;
    }

    public final void counting(int unitNumber) {
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
    public final void waitForAll(ExecutorService exec) {
        exec.shutdown();
        try {
            while (!exec.isTerminated()) {
                exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class CountingUnit implements Runnable {
        private int id;
        private CyclicBarrier barrier;

        CountingUnit(int newId, CyclicBarrier newBarrier) {
            id = newId;
            barrier = newBarrier;
        }

        @Override
        public String toString() {
            return "Thread-" + id;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (count) {
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
