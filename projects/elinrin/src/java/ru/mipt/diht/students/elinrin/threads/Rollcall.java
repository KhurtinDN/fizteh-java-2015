package ru.mipt.diht.students.elinrin.threads;

import ru.mipt.diht.students.elinrin.threads.exception.HandlerOfException;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Rollcall {
    static final int NUMBER_EVENTS = 10;

    private static volatile boolean allSaidYes = false;
    private static CyclicBarrier allReady, allAnswered;

    public static void main(final String[] args) {
        int number;
        number = Parse.parse(args);

        allReady = new CyclicBarrier(number + 1);
        allAnswered = new CyclicBarrier(number + 1);

        for (int i = 0; i < number; i++) {
            RollcallThread thread = new RollcallThread();
            thread.start();
        }

        while (!allSaidYes) {
            System.out.println("Are you ready?");
            allSaidYes = true;
            try {
                allReady.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                HandlerOfException.handler(e);
            }
            allReady.reset();
            try {
                allAnswered.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                HandlerOfException.handler(e);
            }
            allAnswered.reset();

            if (allSaidYes) {
                System.exit(0);
            }
        }
    }

    private static class RollcallThread extends Thread {
        private Boolean answer;
        private Random rand = new Random();

        @Override
        public void run() {
            while (true) {
                try {
                    allReady.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    HandlerOfException.handler(e);
                }
                answer = rand.nextInt(NUMBER_EVENTS) != 0;
                if (answer) {
                    System.out.println("Yes");
                } else {
                    System.out.println("No");
                }
                if (!answer) {
                    allSaidYes = false;
                }
                try {
                    allAnswered.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    HandlerOfException.handler(e);
                }
            }
        }
    }
}
