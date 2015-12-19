package ru.mipt.diht.students.tveritinova.Threads;

import java.util.Random;

public class RollCall {

    public static final int PERCENTAGE = 10;
    private static boolean isAllYes = false;

    public static void main(String[] args) {
        int cnt = Integer.parseInt(args[0]);
        Thread[] threads = new Thread[cnt];

        for (int i = 0; i < cnt; i++) {
            threads[i] = new RollCallThread();
        }

        while (!isAllYes) {
            System.out.println("Are you ready?");
            isAllYes = true;

            for (Thread t: threads) {
                t.run();
            }
        }
    }

    public static class RollCallThread extends Thread {

        @Override
        public final synchronized void run() {

            if (new Random().nextInt(PERCENTAGE) == 1) {
                System.out.println("NO");
                isAllYes = false;
            } else {
                System.out.println("YES");
            }

        }
    }
}
