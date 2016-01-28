package ru.mipt.diht.students.threads;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mikhail on 27.01.16.
 */
class Rollcall {
    private boolean ready = false;
    private final Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private final CyclicBarrier barrierBefore, barrierAfter;
    private final Participant[] participants;

    Rollcall(int participantsNum) {
        participants = new Participant[participantsNum];
        barrierBefore = new CyclicBarrier(participantsNum + 1);
        barrierAfter = new CyclicBarrier(participantsNum + 1);

        ExecutorService exec = Executors.newFixedThreadPool(participants.length);
        for (int i = 0; i < participants.length; ++i) {
            participants[i] = new Participant();
            exec.execute(participants[i]);
        }

        while(!ready) {
            ready = true;
            try {
                System.out.println("Are you ready?");
                barrierBefore.await();
                barrierAfter.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        exec.shutdownNow();
    }

    class Participant implements Runnable {
        synchronized void areYouReady() {
            if (random.nextInt() % 10 == 0) {
                System.out.println("No");
                ready = false;
            } else {
                System.out.println("Yes");
            }
        }

        @Override
        public void run() {
            while(true) {
                try {
                    barrierBefore.await();
                    areYouReady();
                    barrierAfter.await();
                } catch (InterruptedException | BrokenBarrierException ignored) {
                    break;
                }
            }
        }
    }
}
