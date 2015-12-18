package ru.fizteh.fivt.students.bulgakova.Threads;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;


public class Mainclass {

    public static void main(String args[]) throws InterruptedException {

        for (int i = 0; i < 3; i++) {
            Rhymes rhymes = new Rhymes(5);
        }


        Muster muster = new Muster(5);
        muster.Asking();


        BlockingQueue<Integer> blockingQueue = new BlockingQueue<Integer>(5);
        List<Integer> list1 = new ArrayList<Integer>();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        list1.add(4);
        list1.add(5);

        List<Integer> myList2;
        try {
            blockingQueue.offer(list1);
            myList2 = blockingQueue.take(2);
            for(Integer i : myList2) {
                System.out.print(i + " ");
            }
        } catch (LimitExceededException e) {
            e.printStackTrace();
        }
    }
}