package projects.JenkaEff.src.main.java.ru.mipt.diht.students.JenkaEff.threads;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Threads2 {

    private final static int THREADS_NUM = 10;
    private final static double PROB = 0.9;
    private final static String NEGATIVE_RESPONSE = "No";
    private final static String POSITIVE_RESPONSE = "Yes";

    private static Lock[] mutexes = new ReentrantLock[THREADS_NUM];
    private static boolean[] responses = new boolean[THREADS_NUM];
    private static Object cv = new Integer(0);

    static class Child implements Runnable {

        private int number;
        private Random rand;

        Child(int num) {
            number = num;
            rand = new Random();
        }
        
        private boolean getRandom(double prob) {
            return rand.nextDouble() > prob;
        }
        
        @Override
        public void run() {
            while (true) {
                synchronized (cv) {

                    try {
                        cv.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                    mutexes[number].lock();
                    try {
                        responses[number] = getRandom(PROB);
                        if (responses[number]) {
                            System.out.println(POSITIVE_RESPONSE + number);
                        } else {
                            System.out.println(NEGATIVE_RESPONSE + number);
                        }
                    } finally {
                        mutexes[number].unlock();
                    }
                }

            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[THREADS_NUM];
        for (int i = 0; i < THREADS_NUM; i++) {
            threads[i] = new Thread(new Child(i));
            threads[i].start();
            mutexes[i] = new ReentrantLock();
        }
        while (true) {
            synchronized (cv) {

                cv.notifyAll();
                boolean flag = true;
                for (int i = 0; i < THREADS_NUM; i++) {
                    mutexes[i].lock();
                    flag = flag && responses[i];
                    mutexes[i].unlock();
                }
                if (flag) {
                    break;
                }
            }
        }
        for (int i = 0; i < THREADS_NUM; i++) {
            threads[i].interrupt();
        }
    }

}
