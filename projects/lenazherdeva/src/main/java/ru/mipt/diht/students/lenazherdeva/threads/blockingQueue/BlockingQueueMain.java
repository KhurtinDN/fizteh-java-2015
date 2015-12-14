package ru.mipt.diht.students.lenazherdeva.threads.blockingQueue;

import java.util.ArrayList;

/**
 * Created by admin on 15.12.2015.
 */
public class BlockingQueueMain {
    @SuppressWarnings("checkstyle:magicnumber")
    public static void main(String[] args) {
        ArrayList<Integer> toPut = new ArrayList<>();
        toPut.add(1);
        toPut.add(2);
        toPut.add(3);
        toPut.add(4);
        BlockingQueue<Integer> q = new BlockingQueue<>(5);
        try {
            q.offer(toPut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            q.take(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
