package threads.task3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BlockingQueueMain {
    public static final int COUNT = 10;
    public static final int N1 = 1;
    public static final int N2 = 2;
    public static final int N3 = 3;
    public static final int N4 = 4;
    public static final int N5 = 5;
    public static final int N6 = 6;
    public static final int N7 = 7;
    public static final int N8 = 8;
    public static final int N9 = 9;
    public static final int N10 = 10;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {
            System.out.println("\nFirst case - consumer blocked while provider will offer enough elements to queue");
            final BlockingQueue<Integer> blockingQueue1 = new BlockingQueue<>(5);
            final CountDownLatch firstCaseInitTracker = new CountDownLatch(2);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("CONSUMER: try to take 5 elements from queue...");
                    List<Integer> elements = blockingQueue1.take(5);
                    System.out.println("CONSUMER: elements taken: " + elements);
                    firstCaseInitTracker.countDown();
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("PROVIDER: sleep 3 seconds before offering elements to queue...");
                        TimeUnit.SECONDS.sleep(3);
                        System.out.println("PROVIDER: offer 5 elements to queue...");
                        blockingQueue1.offer(Arrays.asList(N1, N2, N3, N4, N5));
                        firstCaseInitTracker.countDown();
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            });
            firstCaseInitTracker.await();

            System.out.println("\nSecond case - provider blocked while consumer will take extra elements from queue");
            final BlockingQueue<Integer> blockingQueue2 = new BlockingQueue<>(5);
            final CountDownLatch secondCaseInitTracker = new CountDownLatch(2);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("PROVIDER: offer 1-5 elements to queue...");
                    blockingQueue2.offer(Arrays.asList(N1, N2, N3, N4, N5));
                    System.out.println("PROVIDER: 1-5 element offered!");
                    System.out.println("PROVIDER: offer 5-10 elements to queue...");
                    blockingQueue2.offer(Arrays.asList(N6, N7, N8, N9, N10));
                    System.out.println("PROVIDER: 5-10 element offered!");
                    secondCaseInitTracker.countDown();
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("CONSUMER: sleep 3 seconds before take elements from queue...");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    System.out.println("CONSUMER: take 5 elements from queue...");
                    System.out.println("CONSUMER: Taken elements: " + blockingQueue2.take(5));
                    secondCaseInitTracker.countDown();
                }
            });
            secondCaseInitTracker.await();

            System.out.println("\nThird case - " +
                    "provider blocked while consumer will take extra elements from queue one by one");
            final BlockingQueue<Integer> blockingQueue3 = new BlockingQueue<>(3);
            final CountDownLatch thirdCaseInitTracker = new CountDownLatch(2);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("PROVIDER: offer 10 elements to queue one by one...");
                    for (int i = 1; i <= COUNT; i++) {
                        System.out.println("PROVIDER: offer [" + i + "] element to queue...");
                        blockingQueue3.offer(Collections.singletonList(i));
                    }
                    System.out.println("PROVIDER: all 10 elements offered to queue!");
                    thirdCaseInitTracker.countDown();
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("CONSUMER: take 10 elements from queue one by one...");
                    for (int i = 1; i <= COUNT; i++) {
                        System.out.println("CONSUMER: taken " +  blockingQueue3.take(1) + " element from queue...");
                        // sleep 1 second before next taking...
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                    System.out.println("CONSUMER: all 10 elements are taken from queue!");
                    thirdCaseInitTracker.countDown();
                }
            });
            thirdCaseInitTracker.await();
        } finally {
            executorService.shutdown();
        }
    }
}
