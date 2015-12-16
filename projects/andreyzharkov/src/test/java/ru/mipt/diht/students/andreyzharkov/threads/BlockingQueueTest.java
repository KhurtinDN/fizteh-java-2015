package ru.mipt.diht.students.andreyzharkov.threads;

import junit.framework.TestCase;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Андрей on 16.12.2015.
 */
public class BlockingQueueTest extends TestCase {
    @Test
    public final void testBlocking() {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        try {
            queue.offer(Arrays.asList(1, 2, 3));
        } catch (InterruptedException | TimeoutException e) {
            fail(e.getMessage());
        }
        try {
            queue.offer(Arrays.asList(4, 5, 6));
        } catch (InterruptedException | TimeoutException e) {
            fail(e.getMessage());
        }
        try {
            queue.offer(Arrays.asList(7, 8, 9));
        } catch (InterruptedException | TimeoutException e) {
            fail(e.getMessage());
        }

        new Thread(() -> {
            try {
                queue.offer(Arrays.asList(0, 0));
            } catch (InterruptedException | TimeoutException e) {
                fail(e.getMessage());
            }
        }).start();

        try {
            List<Integer> check = queue.take(2);
            assertEquals("[1, 2]", check.toString());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        try {
            List<Integer> check = queue.take(4);
            assertEquals("[3, 4, 5, 6]", check.toString());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        try {
            List<Integer> check = queue.take(5);
            assertEquals("[7, 8, 9, 0, 0]", check.toString());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        BlockingQueue<Integer> bigQueue = new BlockingQueue<>(1000);
        try {
            for (int i = 0; i < 500; i++) {
                bigQueue.offer(Arrays.asList(i, i + 1));
            }

            new Thread(() -> {
                try {
                    bigQueue.offer(Arrays.asList(-1, -2, -3));
                } catch (InterruptedException | TimeoutException e) {
                    fail(e.getMessage());
                }
            }).start();
            bigQueue.take(1000);
            assertEquals("[-1, -2, -3]", bigQueue.take(3).toString());
        } catch (InterruptedException | TimeoutException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public final void testTimeoutBlocking() {
        BlockingQueue<Integer> queue = new BlockingQueue<>(7);
        try {
            queue.offer(Arrays.asList(1, 2, 3, 4), 20);
        } catch (InterruptedException | TimeoutException e) {
            fail(e.getMessage());
        }
        try {
            queue.offer(Arrays.asList(5, 6, 7, 8), 20);
            fail("no exceptions throwned");
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (TimeoutException e) {
        }


        new Thread(() -> {
            try {
                Thread.sleep(40);
                queue.offer(Arrays.asList(9, 10));
            } catch (InterruptedException | TimeoutException e) {
                fail(e.getMessage());
            }
        }).start();

        try {
            List<Integer> result = queue.take(5, 1000);
            assertEquals("[1, 2, 3, 4, 9]", result.toString());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
}
