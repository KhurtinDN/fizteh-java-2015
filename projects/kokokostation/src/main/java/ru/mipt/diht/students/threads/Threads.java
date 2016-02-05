package ru.mipt.diht.students.threads;

/**
 * Created by mikhail on 26.01.16.
 */
public class Threads {
    public static void main(String[] args) throws InterruptedException {
        int num = Integer.parseInt(args[0]);

        new Rollcall(num);
        Thread.sleep(2000);

        new Rhymes(num);
    }
}
