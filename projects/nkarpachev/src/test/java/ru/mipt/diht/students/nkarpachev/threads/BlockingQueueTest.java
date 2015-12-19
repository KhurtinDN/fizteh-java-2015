package ru.mipt.diht.students.nkarpachev.threads;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static org.junit.Assert.*;

public class BlockingQueueTest {
    private int lastAdded;
    BlockingQueue<Integer> testQueue = new BlockingQueue<>(10);

    @Test
    public void testQueue() {
        TestThread thread1 = new TestThread("put", 10, 5);
        TestThread thread2 = new TestThread("get", 7, 5);
        try {
            thread1.run();
            thread2.run();
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public class TestThread extends Thread {
        private String threadAction;
        private int actionsNumber;
        private int number;

        TestThread(String action, int actionsNumber, int value) {
            threadAction = action;
            number = value;
        }

        @Override
        public final void run() {
            if (Objects.equals(threadAction, "put")) {
                List<Integer> offerList = new LinkedList<>();
                for (int i = 0; i < actionsNumber; i++) {
                    offerList.add(new Integer(number + i));
                }
                testQueue.offer(offerList);
            }
            else {
                List<Integer> output;
                output = testQueue.take(number);
                for (int i = 0; i < output.size(); i++) {
                    assertEquals(new Integer(lastAdded + i), output.get(i));
                }
            }
        }
    }
}