package ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions;

/**
 * Created by tulindanil on 23.09.15.
 */

public class NoKeyException extends Exception {
    public NoKeyException() {
        super("Something went terribly wrong: no maps "
                + "key found");
    }
}
