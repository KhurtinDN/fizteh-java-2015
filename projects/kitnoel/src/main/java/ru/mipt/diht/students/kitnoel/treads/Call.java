package ru.mipt.diht.students.kitnoel.treads;

/**
 * Created by leonk on 19.12.15.
 */
import java.util.Random;

public class Call {

    public static class Runner {
        private static int curN = 0;
        private static boolean finish = false;
        private static  boolean alive = true;
        private static Random random = new Random();

        private static class ThreadClass extends Thread {
            private volatile int number;

            @Override
            public void run() {
                while (alive) {
                    if (curN + 1 == number) {
                        int x = random.nextInt(10);
                        if (x < 1) {
                            finish = false;
                            System.out.println("No");
                        } else {
                            System.out.println("Yes");
                        }
                        curN++;
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            ThreadClass(int num) {
                this.number = num;
            }
        }

        void run(int n) {
            random = new Random();
            curN = n;
            ThreadClass[] threads = new ThreadClass[n];
            for (int i = 0; i < n; i++) {
                threads[i] = new ThreadClass(i + 1);
                threads[i].start();
            }
            while (true) {
                if (curN == n) {
                    if (finish) break;
                    finish = true;
                    System.out.println("Are you ready?");
                    curN = 0;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            alive = false;
        }
    }

    public static void main(String[] args) {
        int n = 0;
        if (args.length > 0) {
            n = Integer.valueOf(args[0]);
        }
        new Runner().run(n);
    }
}
