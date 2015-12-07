package ru.mipt.diht.students.ale3otik.moduleTests.lockingQueueTest;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.threads.lockingqueue.LockingQueue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by alex on 06.12.15.
 */
public class LockingQueueTest extends TestCase {

    private static int MAX_QUEUE_SIZE = 30;
    private List<Integer> baseList;
    private LockingQueue<Integer> queue;

    @Before
    public void setUp() {
        baseList = new LinkedList<>();
        for (int i = 0; i < MAX_QUEUE_SIZE - 10; ++i) {
            baseList.add(i);
        }

        queue = new LockingQueue<>(MAX_QUEUE_SIZE);
    }

    private static class ThreadTaker extends Thread {
        volatile LockingQueue<Integer> queue;
        volatile List<Integer> answer = Arrays.asList(-1);

        ThreadTaker(LockingQueue<Integer> rcvQueue) {
            queue = rcvQueue;
        }

        @Override
        public void run() {
            answer = queue.take(4, 300);
        }
    }

    private static class ThreadOffer extends Thread {
        volatile LockingQueue<Integer> queue;
        List<Integer> toAdd;

        ThreadOffer(LockingQueue<Integer> rcvQueue, List<Integer> toAddList) {
            queue = rcvQueue;
            toAdd = toAddList;
        }

        @Override
        public void run() {
            queue.offer(toAdd, 300);
        }
    }

    @Test
    public void testSimpleRequestQueue() throws Exception {
        queue.offer(baseList);
        assertEquals(queue.take(baseList.size()), baseList);
    }

    @Test
    public void testTakeDelay() throws Exception {
        queue.offer(Arrays.asList(0, 0));
        ThreadTaker taker = new ThreadTaker(queue);
        taker.start();
        queue.offer(baseList);
        Thread.sleep(100);
        assertEquals(taker.answer, Arrays.asList(0, 0, 0, 1));
        assertEquals(queue.take(1), Arrays.asList(2));

    }

    @Test
    public void testTakeDelaySkip() throws Exception {
        queue.offer(Arrays.asList(0, 1));
        List<Integer> answer = queue.take(4, 200);
        assertEquals(queue.take(2), Arrays.asList(0, 1));
        assertEquals(answer, null);
    }

    @Test
    public void testOfferDelay() throws Exception {
        queue.offer(baseList);
        ThreadOffer offer = new ThreadOffer(queue, baseList);
        offer.start();

        queue.take(20);
        Thread.sleep(100);
        assertEquals(queue.take(20), baseList);
    }

    @Test
    public void testOfferDelaySkip() throws Exception {
        queue.offer(baseList);
        queue.offer(baseList, 200);
        queue.offer(Arrays.asList(0, 1));
        assertEquals(queue.take(20), baseList);
        assertEquals(queue.take(2), Arrays.asList(0, 1));
    }
}

