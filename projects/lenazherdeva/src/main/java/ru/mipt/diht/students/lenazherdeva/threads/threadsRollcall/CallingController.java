package ru.mipt.diht.students.lenazherdeva.threads.threadsRollcall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by admin on 13.12.2015.
 */
public class CallingController {
    private int numberOfThreads;
    private List<AnsweringThreads> answeringThreadsList;
    private CyclicBarrier cyclicBarrier;
    private boolean ready;


    CallingController(int number) {
        numberOfThreads = number;
        answeringThreadsList = new ArrayList<>(number);
        cyclicBarrier = new CyclicBarrier(numberOfThreads + 1, new Runnable() {
            @Override
            public void run() {
                ready = true;
                for (int i = 0; i < answeringThreadsList.size(); ++i) {
                    ready  &= answeringThreadsList.get(i).getAnswer();
                }
                if (!ready) {
                    System.out.println("Are you ready?");
                }
            }
        });
    }

    public final boolean isReady() {
        return ready;
    }

    public final CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }

    public final void start() throws BrokenBarrierException, InterruptedException {
        System.out.println("Are you ready?");
        for (int i = 0; i < numberOfThreads; ++i) {
            answeringThreadsList.add(new AnsweringThreads(this));
            answeringThreadsList.get(i).start();
        }
        while (!ready) {
            cyclicBarrier.await();
        }
    }
}
