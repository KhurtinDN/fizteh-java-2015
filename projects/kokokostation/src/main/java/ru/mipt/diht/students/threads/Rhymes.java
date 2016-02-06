package ru.mipt.diht.students.threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mikhail on 27.01.16.
 */
class Rhymes {
    private final CountDownLatch countDownLatch;
    private final Participant[] participants;

    Rhymes(int participantsNum) {
        countDownLatch = new CountDownLatch(participantsNum);
        participants = new Participant[participantsNum];

        ExecutorService exec = Executors.newFixedThreadPool(participants.length);
        for (int i = 0; i < participants.length; ++i) {
            participants[i] = new Participant(i);
            exec.execute(participants[i]);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (participants[0]) {
            participants[0].notify();
        }
    }

    class Participant implements Runnable {
        private final int id;
        private boolean waiting = false;

        public Participant(int id) {
            this.id = id;
        }

        private Participant nextParticipant() {
            return participants[(id + 1) % participants.length];
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (countDownLatch.getCount() != 0) {
                        countDownLatch.countDown();
                    }

                    synchronized (this) {
                        if (!waiting) {
                            waiting = true;
                            wait();
                        }
                    }
                } catch (InterruptedException ignored) {
                }

                synchronized (this) {
                    waiting = false;
                }

                System.out.println("Thread-" + id);

                synchronized (nextParticipant()) {
                    if (nextParticipant().waiting) {
                        nextParticipant().notify();
                    } else {
                        nextParticipant().waiting = true;
                    }
                }
            }
        }
    }
}