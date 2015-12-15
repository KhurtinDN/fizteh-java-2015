package ru.mipt.diht.students.simon23rus.Threads;


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by semenfedotov on 14.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlockingQueueTest extends TestCase {
    BlockingQueue<Integer> myBlockingQueue;

    @Test
    public void additionTest() {
        myBlockingQueue = new BlockingQueue<Integer>(5);
        List<Integer> toAdd = Arrays.asList(1, 8, 9, 9);
        myBlockingQueue.offer(toAdd);
        List<Integer> myQueueElements = myBlockingQueue.getData();
        assertEquals(toAdd.size(), myQueueElements.size());
        for(int i = 0; i < myQueueElements.size(); ++i) {
            assertEquals(toAdd.get(i), myQueueElements.get(i));
        }
    }

    @Test
    public void deletionTest() {
        myBlockingQueue = new BlockingQueue<Integer>(5);
        List<Integer> toAdd = Arrays.asList(1, 8, 9, 9, 1);
        myBlockingQueue.offer(toAdd);
        List<Integer> yearOfFoundationFCB = myBlockingQueue.take(4);
        List<Integer> myQueueElements = myBlockingQueue.getData();
        assertEquals(toAdd.size(), myQueueElements.size() + 4);
        List<Integer> toCheck = Arrays.asList(1, 8, 9, 9);
        assertEquals(toCheck.size(), yearOfFoundationFCB.size());
        for(int i = 0; i < toCheck.size(); ++i) {
            assertEquals(toCheck.get(i), yearOfFoundationFCB.get(i));
        }
    }

    @Test
    public void notFullLockTest() throws InterruptedException {
        myBlockingQueue = new BlockingQueue<Integer>(5);
        List<Integer> toAdd = Arrays.asList(1, 8, 9, 9, 1, -1);
        Thread adder = new Thread(() -> myBlockingQueue.offer(toAdd));
        adder.start();
        adder.join(567);
        if(adder.isAlive()) {
            //znachit vse ploho, on ozhidaet sihnala na zapis';
            assert(true);
        }
        else
            assert(false);
    }


    @Test
    public void notEmptyLockTest() throws InterruptedException {
        myBlockingQueue = new BlockingQueue<Integer>(5);
        Thread adder = new Thread(() -> myBlockingQueue.take(3));
        adder.start();
        adder.join(567);
        if(adder.isAlive()) {
            //znachit vse ploho, on ozhidaet sihnala na zapis';
            assert(true);
        }
        else
            assert(false);
    }

    @Test
    public void hardUsageTest() throws InterruptedException {
        myBlockingQueue = new BlockingQueue<Integer>(5);
        List<Integer> toAdd = Arrays.asList(1, 8, 9, 9);
        Integer numberForDeletion = 2;
        List<Integer> deletionResult = new ArrayList<Integer>();
        Thread deleter = new Thread(() -> {deletionResult.addAll(myBlockingQueue.take(numberForDeletion));});
        Thread adder = new Thread(() -> myBlockingQueue.offer(toAdd));
        deleter.start(); adder.start();
        adder.join();
        deleter.join();
        assertEquals(2, deletionResult.size());
        for(int i = 0; i < 2; ++i) {
            assertEquals(toAdd.get(i), deletionResult.get(i));
        }
        assertEquals(2, myBlockingQueue.getData().size());
        for(int i = 0; i < 2; ++i) {
            assertEquals(toAdd.get(i + numberForDeletion), myBlockingQueue.getData().get(i));
        }
        System.out.println(myBlockingQueue.getData());

    }
}
