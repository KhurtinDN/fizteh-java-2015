package ru.mipt.diht.students.kitnoel.treads;

/**
 * Created by leonk on 19.12.15.
 */
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Call {

    private static volatile boolean allReady;
    private static boolean finish;
    private static volatile Random chance = new Random();
    private static CyclicBarrier beginAnswer;
    private static CyclicBarrier endAnswer;

    private static class Player extends Thread {

        @Override
        public void run() {
            while (!finish) {
                try {
                    beginAnswer.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                if (chance.nextInt(10) > 1) {
                    System.out.println("Yes");
                } else {
                    System.out.println("No");
                    allReady = false;
                }
                try {
                    endAnswer.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                    ex.printStackTrace();
                }
            }
        }


    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            finish = false;
            int n = Integer.valueOf(args[0]);
            beginAnswer = new CyclicBarrier(n + 1);
            endAnswer = new CyclicBarrier(n + 1);
            Player[] players = new Player[n];
            for (int i = 0; i < n; i++) {
                players[i] = new Player();
                players[i].start();
            }
            finish = false;
            while (!finish) {
                System.out.println("Are you ready?");
                allReady = true;
                try {
                    beginAnswer.await();
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }
                beginAnswer.reset();
                try {
                    endAnswer.await();
                    if (allReady) {
                        finish = true;
                        for (int i = 0; i < n; i++) {
                            players[i].join();
                        }
                        System.exit(0);
                    }
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}