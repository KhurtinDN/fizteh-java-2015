package threads.task1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Counter {

    private static final int DELAY_BETWEEN_COUNTER_SECONDS = 1;

    private static final int DEFAULT_COUNT_OF_THREADS = 5;

    public static void main(String[] args) throws InterruptedException {
        final int countOfThreads;
        if (args.length == 0) {
            countOfThreads = DEFAULT_COUNT_OF_THREADS
        } else {
            countOfThreads = Integer.parseInt(args[0]);
        }
        if (countOfThreads <= 1) {
            throw new IllegalArgumentException("Count of threads must be > 1");
        }


        ExecutorService executorService = Executors.newFixedThreadPool(countOfThreads);
        final CountDownLatch initTracker = new CountDownLatch(countOfThreads);
        final ReentrantLock lock = new ReentrantLock();


        Map<Integer, Condition> conditions = new HashMap<>(countOfThreads);
        for (int i = 1; i <= countOfThreads; i++) {
            Condition iCondition = lock.newCondition();
            conditions.put(i, iCondition);
        }


        for (int i = 1; i <= countOfThreads; i++) {

            final int threadNum = i;

            final Condition myCondition = conditions.get(i);

            final Condition nextCondition;
            if (i == countOfThreads) {
                nextCondition = conditions.get(1)
            } else {
                nextCondition = conditions.get(i + 1);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    try {

                        initTracker.countDown();
                        while (true) {

                            myCondition.await();
                            System.out.println("Thread-" + threadNum);
                            TimeUnit.SECONDS.sleep(DELAY_BETWEEN_COUNTER_SECONDS);

                            nextCondition.signal();
                        }
                    } catch (InterruptedException e) {
                        // finish loop
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }


        initTracker.await();
        try {
            lock.lock();

            conditions.get(1).signal();
        } finally {
            lock.unlock();
        }
    }
}
