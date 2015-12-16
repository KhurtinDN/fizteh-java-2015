package ru.mipt.diht.students.annnvl.Threads.RollCall;

import java.util.Random;

public class RollCallerThread extends Thread {
    private static int numberOfThreads;
    private static volatile int asked = 0;
    private static volatile int timesShouldBe = -1; //какой раз мы проводим опрос
    private static boolean Yes4all = false;
    private static boolean EverybodyOk = true;
    private Random rand;
    private int timesNum;//какой раз мы вызываем конкретный процесс

    RollCallerThread() {
        rand = new Random(100);
        timesNum = 0;
    }

    public static void setNumberOfThreads(int num) {
        numberOfThreads = num;
    }

    public static void Yes() {
        Yes4all = true;
    }

    public static boolean isEverybodyOk() {
        return EverybodyOk;
    }

    public static void makeEverybodyOk() {
        EverybodyOk = true;
    }

    public static void nextRollCall() {
        timesShouldBe++;
        asked = 0;
    }

    public static boolean Everybodyasked() {
        return (asked == numberOfThreads);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (timesNum != timesShouldBe) {
                    Thread.yield();
                }
                if (Yes4all) {
                    return;
                }
                timesNum++;
                if (rand.nextDouble() < 0.1) {
                    System.out.println("No\n");
                    EverybodyOk = false;
                } else {
                    System.out.println("Yes\n");
                }
                asked++;
            }
        }
    }
}
