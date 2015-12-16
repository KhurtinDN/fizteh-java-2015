package ru.mipt.diht.students.annnvl.Threads.Counter;

public class Counter {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Please type number of threads");
        }
        int threads = Integer.parseInt(args[0]);
        CountedThread.setNumberOfThreads(threads);
        for (int i = 0; i < threads; i++) {
            (new CountedThread(i)).start();
        }
    }
}
