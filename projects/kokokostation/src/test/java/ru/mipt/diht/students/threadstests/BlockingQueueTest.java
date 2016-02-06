package ru.mipt.diht.students.threadstests;

import org.junit.Test;
import ru.mipt.diht.students.threads.BlockingQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by mikhail on 29.01.16.
 */
public class BlockingQueueTest {
    @Test
    public void test() throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(3);

        exec.execute(new One(blockingQueue));
        exec.execute(new Two(blockingQueue));

        exec.shutdown();
        while (!exec.awaitTermination(1, TimeUnit.SECONDS)) {
            ;
        }
        assertThat(blockingQueue.size(), is(3));
    }

    @Test
    public void anotherTest() throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(41);
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(7);
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        CountDownLatch countDownLatch = new CountDownLatch(40);

        exec.execute(new Writer(blockingQueue, lock, condition, atomicBoolean, countDownLatch));

        List<Reader> readers = new ArrayList<>();
        for (int i = 0; i < 40; ++i) {
            readers.add(new Reader(blockingQueue, lock, condition, atomicBoolean, countDownLatch));
            exec.execute(readers.get(readers.size() - 1));
        }

        exec.shutdown();
        while (!exec.awaitTermination(1, TimeUnit.SECONDS)) {
            ;
        }

        assertThat(readers.stream().collect(Collectors.summingInt(Reader::getCounter)), is(15002));
    }
}

class One implements Runnable {
    private BlockingQueue<Integer> blockingQueue;

    public One(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1001; i++) {
            try {
                while (!blockingQueue.offer(Arrays.asList(1, 2, 3), 1L)) {
                    ;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Two implements Runnable {
    private BlockingQueue<Integer> blockingQueue;

    public Two(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            try {
                for (int j = 1; j <= 3; ++j) {
                    List<Integer> result;
                    while ((result = blockingQueue.take(1, 1L)).isEmpty()) {
                        ;
                    }
                    assertThat(result.get(0), is(j));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Writer implements Runnable {
    private final BlockingQueue<Integer> blockingQueue;
    private final Lock lock;
    private final Condition condition;
    private final AtomicBoolean endWrite;
    private final CountDownLatch countDownLatch;

    public Writer(BlockingQueue<Integer> blockingQueue, Lock lock, Condition condition,
                  AtomicBoolean endWrite, CountDownLatch countDownLatch) {
        this.blockingQueue = blockingQueue;
        this.lock = lock;
        this.condition = condition;
        this.endWrite = endWrite;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            blockingQueue.offer(Arrays.asList(1, 2, 3, 4), 1L);
            blockingQueue.offer(Arrays.asList(1, 2, 3, 4), 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }

        for (int i = 0; i < 10000; ++i) {
            try {
                while (!blockingQueue.offer(Arrays.asList(1, 2, 3), 1L)) {
                    ;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        endWrite.set(true);
    }
}

class Reader implements Runnable {
    private final BlockingQueue<Integer> blockingQueue;
    private final Lock lock;
    private final Condition condition;
    private final AtomicBoolean endWrite;
    private final CountDownLatch countDownLatch;
    private int counter;

    public Reader(BlockingQueue<Integer> blockingQueue, Lock lock, Condition condition,
                  AtomicBoolean endWrite, CountDownLatch countDownLatch) {
        this.blockingQueue = blockingQueue;
        this.lock = lock;
        this.condition = condition;
        this.endWrite = endWrite;
        this.countDownLatch = countDownLatch;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            countDownLatch.countDown();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        while (!endWrite.get()) {
            try {
                if (!blockingQueue.take(2, 1L).isEmpty()) {
                    ++counter;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}