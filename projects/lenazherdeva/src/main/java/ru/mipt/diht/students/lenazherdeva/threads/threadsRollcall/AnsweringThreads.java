package ru.mipt.diht.students.lenazherdeva.threads.threadsRollcall;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

/**
 * Created by admin on 13.12.2015.
 */

public class AnsweringThreads extends Thread {
    private static final int TEN = 10;
    private boolean callingResult;
    private Random random = new Random();
    private CallingController callingController;

    public AnsweringThreads(CallingController controller) {
        callingController = controller;
    }

    public final boolean getAnswer() {
        return callingResult;
    }

    @Override
    public final void run() {
        while (!callingController.isReady()) {
            if (random.nextInt(TEN) != 1) { //для вероятности 90%
                callingResult = true;
                System.out.println("YES");
            } else {
                callingResult = false;
                System.out.println("NO");
            }
            try {
                callingController.getCyclicBarrier().await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
