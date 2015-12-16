package test.java.ru.mipt.diht.students.IrinaMudrova.Threads;

import org.junit.Test;

import junit.framework.Assert;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import static junit.framework.Assert.assertTrue;

import main.java.ru.mipt.diht.students.IrinaMudrova.Threads.BlockingQueue;

/**
 * Created by Ирина on 16.12.2015.
 */
public class BlockingQueueTest{
    @Test
    public void testOneThread() {
        BlockingQueue<Integer> queue = new BlockingQueue<Integer>(3);
        queue.offer(Arrays.asList(1));
        queue.offer(Arrays.asList(2, 3));
        assertTrue(queue.take(1).get(0) == 1);
        assertTrue(queue.take(1).get(0) == 2);
        assertTrue(queue.take(1).get(0) == 3);
        queue.offer(Arrays.asList(1, 2, 3));
        assertTrue(queue.take(3).get(1) == 2);
    }

    @Test(timeout = 150)
    public void testTwoThreads() {
        BlockingQueue<Integer> queue = new BlockingQueue<Integer>(3);
        queue.offer(Arrays.asList(1));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    queue.offer(Arrays.asList());
                }
                queue.offer(Arrays.asList(2, 3));
            }
        }).start();
        queue.take(2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    queue.offer(Arrays.asList());
                }
                queue.offer(Arrays.asList(4, 5));
                queue.take(3);
            }
        }).start();
        queue.offer(Arrays.asList(7, 7, 7));
    }
}
