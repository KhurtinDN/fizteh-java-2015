package ru.fizteh.fivt.students.tulindanil.threads.rollcall;

/**
 * Created by tulindanil on 10.12.15.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: Counter n, where n is a"
                    + " number of the threads");
        }
        (new Rollcaller()).main(Integer.parseInt(args[0]));
    }
}
