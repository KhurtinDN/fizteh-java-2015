package ru.mipt.diht.students.maxdankow.threads;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class BlockingQueueTest {
    BlockingQueue<String> queue;
    List<String> secondList;
    List<String> firstList;

    @Before
    public void setUp() {
        queue = new BlockingQueue<>(10);

        firstList = new ArrayList<>();
        firstList.add("Srt1");
        firstList.add("Str2");
        firstList.add("Str3");
        firstList.add("Str4");

        secondList = new ArrayList<>();
        secondList.add("Second1");
        secondList.add("Second2");
    }

    @Test
    public void addTest() {
        List<String> expected1 = new ArrayList<>();
        expected1.addAll(firstList);
        expected1.addAll(secondList);

        List<String> expected2 = new ArrayList<>();
        expected2.addAll(secondList);
        expected2.addAll(firstList);

        Thread child1 = new Thread(new Putter(queue, firstList));
        Thread child2 = new Thread(new Putter(queue, secondList));
        child1.start();
        child2.start();
        try {
            List<String> newList = queue.take(6);
            assertThat(newList, anyOf(equalTo(expected1), equalTo(expected2)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void putTooMuch() throws InterruptedException {
        firstList.addAll(firstList);
        firstList.addAll(firstList);
        queue.offer(firstList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void takeTooMuch() throws InterruptedException {
        queue.take(100);
    }

    @Test
    public void singleThread() throws InterruptedException {
        queue.offer(firstList);
        queue.offer(secondList);
        queue.offer(firstList);
        List<String> expected = new ArrayList<>();
        expected.addAll(firstList);
        expected.addAll(secondList);
        expected.addAll(firstList);
        assertThat(queue.take(10), equalTo(expected));
    }

    private class Putter implements Runnable {
        private BlockingQueue<String> blockingQueue;
        private List<String> list;

        public Putter(BlockingQueue<String> blockingQueue, List<String> list) {
            this.blockingQueue = blockingQueue;
            this.list = list;
        }

        @Override
        public void run() {
            try {
                blockingQueue.offer(list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
