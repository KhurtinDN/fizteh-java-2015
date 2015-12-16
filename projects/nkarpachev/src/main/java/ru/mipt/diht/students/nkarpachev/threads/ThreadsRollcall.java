package ru.mipt.diht.students.nkarpachev.threads;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.Random;

public class ThreadsRollcall {

    private static CyclicBarrier threadsStarted;
    private static CyclicBarrier threadsCompleted;
    private static volatile boolean allThreadsReady = true;

    private static class RollcallRunner extends Thread {
        Random generator = new Random();

        RollcallRunner() {}

        @Override
        public void run() {
            try {
                while (true) {
                    threadsStarted.await();
                    int randomSeed = generator.nextInt(10);
                    if (randomSeed <= 1) {
                        allThreadsReady = false;
                        System.out.println("Nope");
                    }
                    else {
                        System.out.println("Yeah");
                    }
                    threadsCompleted.await();
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("Something went wrong");
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        int totalThreads = 0;
        try {
            if (args.length != 1) {
                throw new Exception();
            }
            totalThreads = Integer.parseInt(args[0]);
            if (totalThreads <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Wrong input arguments");
            System.exit(1);
        }

        threadsStarted = new CyclicBarrier(totalThreads + 1);
        threadsCompleted = new CyclicBarrier(totalThreads + 1);

        for (int i = 0; i < totalThreads; i++) {
            RollcallRunner thread = new RollcallRunner();
            thread.start();
        }

        while (true) {
            allThreadsReady = true;
            System.out.println("Ready to roll?");
            try {
                threadsStarted.await();
                threadsStarted.reset();
                threadsCompleted.await();
                threadsCompleted.reset();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                System.exit(1);
            }
            if (allThreadsReady) {
                System.exit(0);
            }
        }
    }
}
