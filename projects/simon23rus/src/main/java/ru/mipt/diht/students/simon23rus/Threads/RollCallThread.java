package ru.mipt.diht.students.simon23rus.Threads;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;


public class RollCallThread extends Thread {
    private  boolean isGoodAnswer;
    private Asker myAsker;

    public RollCallThread(Asker questionMan) {
        this.myAsker = questionMan;
    }


    public boolean getIsGoodAnswer() {
        return isGoodAnswer;
    }


    @Override
    public void run() {
        while (!myAsker.getEnding()) {
            Random myAnswer = new Random();
            if(myAnswer.nextInt(10) == 0) {
                System.out.println("No");
                isGoodAnswer = false;
            }
            else {
                System.out.println("Yes");
                isGoodAnswer = true;
            }
            try {
                //ozhidaem otveta vseh threadov
                myAsker.myBarrier.await();
            } catch (InterruptedException e) {}
            catch (BrokenBarrierException e) {}
        }
    }
}


