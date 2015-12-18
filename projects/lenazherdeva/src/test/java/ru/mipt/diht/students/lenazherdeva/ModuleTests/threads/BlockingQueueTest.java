package ru.mipt.diht.students.lenazherdeva.moduleTests.threads;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.lenazherdeva.threads.blockingQueue.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by admin on 18.12.2015.
 */
public class BlockingQueueTest {
    BlockingQueue<Integer> queue;


    @Before
    public void setUp() {
        queue = new BlockingQueue<>(5);
    }

    @Test
    public void testBlockingOnEmpty() throws Exception {BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        Thread testingThread = new Thread(() -> {
            queue.take(5);
        });
        testingThread.start();
        Thread.sleep(50);
        assertThat(testingThread.isAlive(), is(true));
        testingThread.interrupt();
    }

    @Test
    public void testBlockingOnOverFlowing() throws Exception {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        List<Integer> LargeList = Arrays.asList(1, 2, 3, 4, 5, 6);
        Thread testingThread = new Thread(() -> {
            queue.offer(LargeList);
        });
        testingThread.start();
        Thread.sleep(50);
        assertThat(testingThread.isAlive(), is(true));
        testingThread.interrupt();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongSize() {
        queue.take(-6);
    }
}
