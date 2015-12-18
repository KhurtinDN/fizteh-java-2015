package ru.mipt.diht.students.maxdankow.threads;

import java.util.Random;
import java.util.concurrent.*;

public class RollCaller {
    private static volatile Integer count = 0;

    public static Integer getCount() {
        return count;
    }

    public static void iAmReady() {
        synchronized (count) {
            ++count;
        }
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

    public final void rollCall(int unitNumber) {
        ExecutorService exec = Executors.newCachedThreadPool();
        CyclicBarrier barrier = new CyclicBarrier(unitNumber, () -> {
            synchronized (count) {
                if (count == unitNumber) {
                    System.out.println("NICE =)");
                    exec.shutdownNow();
                } else {
                    synchronized (count) {
                        count = 0;
                        System.out.println("AGAIN!");
                    }
                }
            }
        });
        for (int id = 1; id <= unitNumber; ++id) {
            exec.execute(new RollCallUnit(id, barrier));
        }
        waitForAll(exec);
    }

    private class RollCallUnit implements Runnable {
        private int id;
        private CyclicBarrier barrier;
        private final Random random = new Random();
        private final int randomUpperBound = 99;
        private final int successBound = 10;

        RollCallUnit(int newId, CyclicBarrier newBarrier) {
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
                    if (random.nextInt(randomUpperBound) < successBound) {
                        System.out.println(this + ": No");
                    } else {
                        System.out.println(this + ": Yes");
                        RollCaller.iAmReady();
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
