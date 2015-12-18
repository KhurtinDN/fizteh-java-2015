package ru.fizteh.fivt.students.vruchtel.threads;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Серафима on 16.12.2015.
 */

public class Mainclass {

    public static void main(String args[]) throws InterruptedException {
        //while(true) {
        Rythme rythme = new Rythme(10);
        //}
        RollCall rollCall = new RollCall(10);
        rollCall.Asking();

        MyBlockingQueue<Integer> blockingQueue = new MyBlockingQueue<Integer>(10);
        List<Integer> myList1 = new ArrayList<Integer>();
        myList1.add(1);
        myList1.add(2);
        myList1.add(3);
        myList1.add(4);
        myList1.add(5);
        myList1.add(6);
        myList1.add(7);
        myList1.add(8);
        myList1.add(9);
        myList1.add(10);
        //myList1.add(11);
        List<Integer> myList2 = new ArrayList<Integer>();
        try {
            blockingQueue.offer(myList1);
            myList2 = blockingQueue.take(5);
            for(Integer i : myList2) {
                System.out.print(i + " ");
            }
        } catch (LimitExceededException e) {
            e.printStackTrace();
        }
    }
}
