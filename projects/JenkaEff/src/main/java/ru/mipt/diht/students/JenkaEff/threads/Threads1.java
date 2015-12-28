package projects.JenkaEff.src.main.java.ru.mipt.diht.students.JenkaEff.threads;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class Threads1 {

    final static int THREADS_NUM = 5;

//    static Lock[] mutexes = new ReentrantLock[THREADS_NUM];
    static boolean[] responses = new boolean[THREADS_NUM];
    static Object[] cvs = new Integer[THREADS_NUM];

    static int curNum;

    static class Child implements Runnable {

        private int number;
        private int nextNumber;

        Child(int _number) {
            number = _number;
            nextNumber = (number + 1) % THREADS_NUM;
            
        }

        @Override
        public void run() {
            while (true) {
                synchronized (cvs[number]) {
                    try {
                        cvs[number].wait();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println("Thread-" + number);
                    synchronized (cvs[curNum]) {
                        
                    }
                    synchronized (cvs[nextNumber]) {
                        cvs[nextNumber].notify();
                    }                    
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[THREADS_NUM];
        for (int i = 0; i < THREADS_NUM; i++) {
            cvs[i] = new Integer(0);
            threads[i] = new Thread(new Child(i));
            threads[i].start();
        }
        synchronized (cvs[0]) {
            cvs[0].notify();
        }        
    }

}
