package ru.mipt.diht.students.lenazherdeva.threads.threadsRollcall;

import java.util.concurrent.BrokenBarrierException;

/**
 * Created by admin on 13.12.2015.
 */
public class Rollcall {

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        int numberOfThreads = 0;
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException();
            }
            numberOfThreads = Integer.valueOf(args[0]);
            if (numberOfThreads <= 0) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("There should be positive number as command line argument");
            System.exit(1);
        }
        CallingController callingController = new CallingController(numberOfThreads);
        callingController.start();
    }
}
