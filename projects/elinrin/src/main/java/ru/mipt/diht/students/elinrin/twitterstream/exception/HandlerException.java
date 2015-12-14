package ru.mipt.diht.students.elinrin.twitterstream.exception;

public class HandlerException {
    static final String USER_MOD = "user";

    public static void handler(String message, Throwable cause) {
        System.err.println(message + ". " + cause.getMessage());
        System.exit(1);
    }
    public static void handler(Throwable cause) {
        System.err.println(cause.getMessage());
        System.exit(1);
    }

    public static void handler(Throwable cause, String mod) {
        if (mod.equals(USER_MOD)) {
            System.err.println(cause.getMessage());
        } else {
            System.err.println(cause.getMessage());
            System.exit(1);
        }
    }
}
