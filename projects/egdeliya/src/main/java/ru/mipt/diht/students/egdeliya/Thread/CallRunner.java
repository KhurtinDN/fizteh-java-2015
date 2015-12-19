package ru.mipt.diht.students.egdeliya.Thread;

/**
 * Created by Эгделия on 17.12.2015.
 */
public class CallRunner {
    public static void main(String[] args) {
        Call call = new Call();
        call.threadsCall(args[0]);
    }
}
