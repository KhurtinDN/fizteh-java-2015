package ru.mipt.diht.students.pitovsky.threads.tests;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import junit.framework.TestCase;
import ru.mipt.diht.students.pitovsky.threads.BlockingQueue;

public class BlockingQueueTest extends TestCase {

    @Test
    public void testSimpleBlocking() {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        try {
            queue.offer(Arrays.asList(1, 2, 3));
        } catch (InterruptedException | TimeoutException e) {
            fail(e.getMessage());
        }
        new Thread(() -> {
            try {
                queue.offer(Arrays.asList(4, 5, 6));
            } catch (InterruptedException | TimeoutException  e) {
                fail(e.getMessage());
            }
        }).start();
        try {
            queue.take(2);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        try {
            List<Integer> result = queue.take(3);
            assertEquals("[3, 4, 5]", result.toString());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTimeoutBlocking() {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        try {
            queue.offer(Arrays.asList(1, 2), 20);
        } catch (InterruptedException | TimeoutException e) {
            fail(e.getMessage());
        }
        try {
            queue.offer(Arrays.asList(4, 5, 6, 7), 20);
            fail("timeout was exceeded, but no exceptions throwned.");
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (TimeoutException e) {}
        new Thread(() -> {
            try {
                Thread.sleep(40);
                queue.offer(Arrays.asList(10, 11));
            } catch (InterruptedException | TimeoutException  e) {
                fail(e.getMessage());
            }
        }).start();
        try {
            List<Integer> result = queue.take(3, 5000);
            assertEquals("[1, 2, 10]", result.toString());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
}
