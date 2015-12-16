package ready;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by lokotochek on 13.12.15.
 */

public class Ready {

    static ArrayList slavesList;

    public static int numberOfThreads;
    public static boolean dieMthrfckrs;
    public static Integer counter;

    public static void terminateThreads() {
        dieMthrfckrs = true;

        for (int i = 0; i < numberOfThreads; ++i) {
            ReadySlave slave = (ReadySlave) slavesList.get(i);
            try {
                slave.join();
            } catch (InterruptedException e) { }
        }

        System.out.println("all threads are closed!");
    }

    public static void go(int number) {

        slavesList = new ArrayList();
        numberOfThreads = number;

        counter = 0;
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads, () -> {
            synchronized (counter) {
                if (counter == numberOfThreads) {
                    System.out.println("DONE!" + numberOfThreads + "/" + numberOfThreads);
                    dieMthrfckrs = true;
                    return;
                } else {
                    System.out.println("NOT DONE!");
                }
                try {
                    Thread.sleep(1000 * 1);
                } catch (InterruptedException e) { }
                counter = 0;
            }
        });

        for (int i = 0; i < numberOfThreads; ++i) {
            slavesList.add(new ReadySlave());
            ReadySlave slave = (ReadySlave) slavesList.get(i);
            slave.setNumber(i);
            slave.setBarrier(barrier);
            slave.start();
        }

        barrier.reset();

        int time = 0;
        while (!dieMthrfckrs && time < numberOfThreads * 3) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
            ++time;
        }

        terminateThreads();

    }
}

class ReadySlave extends Thread {

    public void setNumber(int number) {
        myNumber = number;
    }

    public void setBarrier(CyclicBarrier cb) {
        barrier = cb;
    }

    CyclicBarrier barrier;
    public int myNumber;

    @Override
    public void run() {
        while (!Ready.dieMthrfckrs) {

            Random random = new Random();
            OptionalInt randomNumber = random.ints(0, Ready.numberOfThreads).findFirst();
            if (randomNumber.getAsInt() == 3) {
                // NO with 1/10 chance
                System.out.println("NO: " + (myNumber + 1));
            } else {
                // YES
                synchronized (Ready.counter) {
                    Ready.counter++;
                    System.out.println("YES: " + (myNumber + 1));
                }
            }
            try {
                barrier.await();
            } catch (Exception e) { }
        }
    }

}

