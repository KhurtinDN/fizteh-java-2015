package ru.mipt.diht.students.ale3otik.twitter.exceptions;

/**
 * Created by alex on 10.10.15.
 */
public class ExitException extends Exception {
    private String message = "MyExitException";

    public ExitException() {
    }

    @Override
    public final String getMessage() {
        return message;
    }
}
