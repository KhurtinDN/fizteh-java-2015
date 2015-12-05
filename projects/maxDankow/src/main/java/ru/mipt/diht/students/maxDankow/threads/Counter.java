package ru.mipt.diht.students.maxDankow.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Counter {
    public static volatile Integer count = 0;

    public void counting(int unitNumber) {
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
        // todo: ожидать окончания всех потоков.
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
