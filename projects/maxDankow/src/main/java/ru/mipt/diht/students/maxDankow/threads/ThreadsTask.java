package ru.mipt.diht.students.maxDankow.threads;

public class ThreadsTask {
    public static void main(String[] args) {
        Counter counter = new Counter();
        counter.counting(50);
        counter.counting(40);
        counter.counting(30);
        counter.counting(20);

        RollCaller caller = new RollCaller();
        caller.rollCall(10);
    }
}
