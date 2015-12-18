package threads.task2;

import java.util.concurrent.*;

public class RollCall {

    private static final int DELAY_BETWEEN_CALL_SECONDS = 1;

    private static final int DEFAULT_COUNT_OF_THREADS = 10;

    private static final int CONST10 = 10;

    private static int tries;

    public static void main(String[] args) throws InterruptedException {
        final int countOfThreads;
        if (args.length == 0) {
            countOfThreads = DEFAULT_COUNT_OF_THREADS;
        } else {
            countOfThreads = Integer.parseInt(args[0]);
        }
        if (countOfThreads <= 1) {
            throw new IllegalArgumentException("Count of threads must be > 1");
        }

        while (true) {
            tries++;
            ExecutorService executorService = Executors.newFixedThreadPool(countOfThreads);

            final CountDownLatch initTracker = new CountDownLatch(countOfThreads);
            final CyclicBarrier readyBarrier = new CyclicBarrier(countOfThreads, new Runnable() {
                @Override
                public void run() {
                    System.out.println("All ready with " + tries + " tries!");
                    System.exit(1);
                }
            });

            try {
                System.out.println("\nAre you ready?");
                for (int i = 0; i < countOfThreads; i++) {
                    final int currentNum = i;

                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            boolean yes = isReady();
                            if (yes) {
                                System.out.println("Thread#" + (currentNum + 1) + ": Yes");
                                initTracker.countDown();
                                try {
                                    readyBarrier.await();
                                } catch (InterruptedException | BrokenBarrierException e) {
                                    // ignore exception
                                }
                            } else {
                                System.out.println("Thread#" + (currentNum + 1) + ": No");
                                initTracker.countDown();
                            }
                        }
                    });
                }

                initTracker.await();
                // continue with a small pause
                TimeUnit.SECONDS.sleep(DELAY_BETWEEN_CALL_SECONDS);
            } finally {
                readyBarrier.reset();
                executorService.shutdown();
            }
        }
    }

    /**
     * @return true - 90%  false - 10%
     */
    private static boolean isReady() {
        int num = (int) (Math.random() * CONST10);
        int anotherNum = (int) (Math.random() * CONST10);
        return num != anotherNum;
    }
}
