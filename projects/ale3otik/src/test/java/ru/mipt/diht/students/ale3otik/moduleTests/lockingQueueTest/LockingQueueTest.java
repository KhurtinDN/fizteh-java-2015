package ru.mipt.diht.students.ale3otik.moduleTests.lockingQueueTest;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.threads.lockingqueue.LockingQueue;

import java.util.Arrays;

/**
 * Created by alex on 06.12.15.
 */
public class LockingQueueTest extends TestCase {

        private static class ThreadTakeTest extends Thread{
            LockingQueue<Integer> queue;
            ThreadTakeTest(LockingQueue<Integer> rcvQueue) {
                queue = rcvQueue;
            }
            @Override
            public void run() {
                System.out.println(queue.take(7));
            }
        }

        @Test
        public static void test(String[] args) throws Exception{
            LockingQueue<Integer> queue = new LockingQueue<>(20);
            ThreadTakeTest takeTest = new ThreadTakeTest(queue);
            queue.offer(Arrays.asList(1, 2, 3));
            System.out.println("start");
            takeTest.start();
            Thread.sleep(2000);
            System.out.println("finish");
            queue.offer(Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12));
            System.out.println(queue.take(2));
        }
}

