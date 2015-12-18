package ru.fizteh.fivt.students.vruchtel.threads;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Серафима on 17.12.2015.
 */
public class RollCall {
    private boolean allThreadsAreReady;
    private int threadsCount;
    private ArrayList<Boolean> answers;

    public class AnsweringFlow extends Thread{
        private int index;

        public AnsweringFlow(int _index) {
            index = _index;
        }

        @Override
        public void run() {
            double random = new Random().nextDouble();
            if(random > 0.1) {
                System.out.println("Yes");
                answers.set(index, Boolean.TRUE);
            } else {
                System.out.println("No");
                answers.set(index, Boolean.FALSE);
            }
        }
    }

    public RollCall(int _threadsCount) {
        threadsCount = _threadsCount;
    }

    public void Asking() throws InterruptedException{
        allThreadsAreReady = false;

        // Массив, в котором будем запоминать ответы всех потоков
        answers = new ArrayList<Boolean>();
        for(int i = 0; i < threadsCount; i++) {
            answers.add(false);
        }

        // Цикл, пока все потоки не скажут, что они "готовы"
        while (!allThreadsAreReady) {
            ArrayList<AnsweringFlow> answeringThreads = new ArrayList<AnsweringFlow>();
            System.out.println("\nAre you ready?");

            for(int i = 0; i < threadsCount; i++) {
                answeringThreads.add(new AnsweringFlow(i));
            }

            for(AnsweringFlow answeringFlow: answeringThreads) {
                answeringFlow.run();
            }

            for(AnsweringFlow answeringFlow: answeringThreads) {
                try {
                    answeringFlow.join();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }

            allThreadsAreReady = true;
            for(Boolean answer : answers) {
                if(!answer) {
                    allThreadsAreReady = false;
                    break;
                }
            }
        }
    }
}
