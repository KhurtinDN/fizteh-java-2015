package ru.mipt.diht.students.annnvl.Threads.RollCall;

import java.util.Random;

public class RollCallerThread extends Thread {
    private static final int HUNDREED = 100;
    private static final double LOWERBOUND = 0.1;
    private static int numberOfThreads;
    private static volatile int asked = 0;
    private static volatile int timesShouldBe = -1; //какой раз мы проводим опрос
    private static boolean yes4all = false;
    private static boolean everybodyOk = true;
    private Random rand;
    private int timesNum; //какой раз мы вызываем конкретный процесс

    RollCallerThread() {
        rand = new Random(HUNDREED);
        timesNum = 0;
    }

    public static void setNumberOfThreads(int num) {
        numberOfThreads = num;
    }

    public static void yes() {
        yes4all = true;
    }

    public static boolean isEverybodyOk() {
        return everybodyOk;
    }

    public static void makeEverybodyOk() {
        everybodyOk = true;
    }

    public static void nextRollCall() {
        timesShouldBe++;
        asked = 0;
    }

    public static boolean everybodyasked() {
        return (asked == numberOfThreads);
    }

    @Override
    public final void run() {
        while (true) {
            synchronized (this) {
                while (timesNum != timesShouldBe) {
                    Thread.yield();
                }
                if (yes4all) {
                    return;
                }
                timesNum++;
                if (rand.nextDouble() < LOWERBOUND) {
                    System.out.println("No");
                    everybodyOk = false;
                } else {
                    System.out.println("Yes");
                }
                asked++;
            }
        }
    }
}
