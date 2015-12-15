package ru.mipt.diht.students.simon23rus.Threads;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by semenfedotov on 13.12.15.
 */
public class BlockingQueueRunner {
    public static void main(String[] args) {
        BlockingQueue forExample = new BlockingQueue(3);
        List myList = new ArrayList<Integer>();
        myList.add(1); myList.add(2); myList.add(3);
        forExample.offer(myList);
        List returned = forExample.take(2);
        forExample.printQueue();
        System.out.println(returned);
    }
}
