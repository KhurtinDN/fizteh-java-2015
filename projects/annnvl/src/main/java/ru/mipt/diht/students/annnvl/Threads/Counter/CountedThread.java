package ru.mipt.diht.students.annnvl.Threads.Counter;

public class CountedThread extends Thread{
    private static int numberOfThreads;
    private static volatile int currentThread = 0;
    public static void setNumberOfThreads(int num){
        numberOfThreads = num;
    }

    private final int myNumber;
    CountedThread(int mynum){
        myNumber = mynum;
    }

    @Override
    public void run(){
        while (true){
            while (myNumber !=currentThread){
                Thread.yield();
            }
            System.out.printf("Thread-%d\n", myNumber+1);
            currentThread = (currentThread+1)%numberOfThreads;
        }
    }
}
