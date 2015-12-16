package counter;

import java.util.ArrayList;

/**
 * Created by lokotochek on 13.12.15.
 */

public class Counter {
    static ArrayList slavesList;

    public static int nextNumber;
    public static int numberOfThreads;
    public static boolean dieMthrfckrs;

    public static void incNextNumber() {
        nextNumber++;
        if (nextNumber == numberOfThreads) {
            nextNumber = 0;
        }
    }

    public static void go(int number) {

        slavesList = new ArrayList();
        numberOfThreads = number;
        nextNumber = 0;

        for (int i = 0; i < numberOfThreads; ++i) {

            slavesList.add(new CounterSlave());
            CounterSlave slave = (CounterSlave) slavesList.get(i);
            slave.setNumber(i);
            slave.start();

        }


        try {
            Thread.sleep(numberOfThreads * 1000);
        } catch (InterruptedException e) { }

        dieMthrfckrs = true;

        for (int i = 0; i < numberOfThreads; ++i) {
            CounterSlave slave = (CounterSlave) slavesList.get(i);
            try {
                slave.join();
            } catch (InterruptedException e) { }
        }

        System.out.println(numberOfThreads + " seconds left, all threads are closed!");

    }
}

class CounterSlave extends Thread {
    public int myNumber;

    public void setNumber(int number) {
        myNumber = number;
    }

    @Override
    public void run() {
        while (!Counter.dieMthrfckrs) {
            while (Counter.nextNumber != myNumber) {
                Thread.yield(); //Передать управление другим потокам
            }
            System.out.println("Thread-" + (myNumber + 1));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { }
            Counter.incNextNumber();
        }
    }
}

