package ru.mipt.diht.students.ale3otik.twitter.exceptions;

/**
 * Created by alex on 05.10.15.
 */
public class LocationException extends Exception {
    private String message = "Location Exception";

    public LocationException(String newMessage) {
        this.message = newMessage;
    }

    @Override
    public final String getMessage() {
        return message;
    }
}
