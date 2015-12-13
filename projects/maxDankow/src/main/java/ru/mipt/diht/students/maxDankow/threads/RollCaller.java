package ru.mipt.diht.students.maxDankow.threads;

import java.util.Random;
import java.util.concurrent.*;

public class RollCaller {
    public static volatile Integer count = 0;

    public static void iAmReady() {
        synchronized (count) {
            ++count;
        }
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

    public void rollCall(int unitNumber) {
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
        int id;
        CyclicBarrier barrier;
        final Random random = new Random();

        public RollCallUnit(int id, CyclicBarrier barrier) {
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
                    if (random.nextInt(99) < 10) {
                        System.out.println(this + "- No");
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
