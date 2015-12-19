package ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions;

/**
 * Created by tulindanil on 06.10.15.
 */
public class TwitterStreamException extends Exception {
    public TwitterStreamException(String message) {
        super(message);
    }

    public TwitterStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
