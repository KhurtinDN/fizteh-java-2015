package ru.mipt.diht.students.glutolik.BlockingQueueTests;

import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.glutolik.Threads.BlockingQueue;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by glutolik on 18.12.15.
 */
public class BlockingQueueTests {
    BlockingQueue<String> queue;
    List<String> first;
    List<String> second;

    @Before
    public void setUp(){
        queue = new BlockingQueue<>(20);

        first = new ArrayList<>();
        first.add("Hello");
        first.add("NiceToMeetYou");
        first.add("How u doing?");
        first.add("Goodbye");

        second = new ArrayList<>();
        second.add("Please come again");
        second.add("Are you kidding me?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void overflow() throws InterruptedException{
        first.addAll(first);
        first.addAll(first);
        first.addAll(second);
        first.addAll(first);
        first.addAll(second);
        first.addAll(second); // first now contains 22 elements

        queue.offer(first);
    }

    @Test(expected = IllegalArgumentException.class)
    public void takeMoreThanExists() throws InterruptedException{
        queue.take(21);
    }

    @Test
    public void offeringTakingTest() throws InterruptedException {
        queue.offer(second);
        List<String> taken = queue.take(2);
        assertEquals(taken, second);
    }
}
