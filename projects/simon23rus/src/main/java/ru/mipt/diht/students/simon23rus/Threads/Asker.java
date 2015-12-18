package ru.mipt.diht.students.simon23rus.Threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by semenfedotov on 07.12.15.
 */
public class Asker {
    private final String READY = "Are you ready?";
    private final int threadsNumber;
    private boolean isEnd = false;
    List<RollCallThread> myAnswerers;
    public CyclicBarrier myBarrier;

    public boolean getEnding(){
        return isEnd;
    }

    public Asker (int threadsNumber) {
        this.threadsNumber = threadsNumber;
        myAnswerers = new ArrayList<RollCallThread>();
        myBarrier = new CyclicBarrier(threadsNumber + 1, new Runnable() {
            public  void run() {
                    isEnd = true;
                    for(int i = 0; i < myAnswerers.size(); ++i) {
                        isEnd &= myAnswerers.get(i).getIsGoodAnswer();
                        System.out.println("This is the end? " + isEnd);
                    }
                    if(isEnd == false) {
                        System.out.println(READY);
                    }
                }
        });
    }



    public void create() {
        System.out.println(READY);
        for(int i = 0; i < threadsNumber; ++i) {
            myAnswerers.add(new RollCallThread(this));
            myAnswerers.get(i).start();
        }
        System.out.println("myAnswerers Size" + myAnswerers.size());
        while(!isEnd) {
            try {
                myBarrier.await();
            } catch (InterruptedException e) {
            } catch (BrokenBarrierException e) {
            }
        }
    }
}
