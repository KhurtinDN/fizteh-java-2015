package ru.mipt.diht.students.simon23rus.Threads;

/**
 * Created by semenfedotov on 07.12.15.
 */
public class RollCallRunner {
    public static void main(String[] args) {
        int numberOfThreads = Integer.valueOf(args[1]);
        Asker mainAsker = new Asker(numberOfThreads);
        mainAsker.create();
    }
}
