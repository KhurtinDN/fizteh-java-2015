package ru.mipt.diht.students.elinrin.threads.exception;

public class HandlerOfException {
    static final String USER_MOD = "user";

    public static void handler(final String message, final Throwable cause) {
        System.err.println(message + ". " + cause.getMessage());
        System.exit(1);
    }
    public static void handler(final Throwable cause) {
        System.err.println(cause.getMessage());
        System.exit(1);
    }

    public static void handler(final Throwable cause, final String mod) {
        if (mod.equals(USER_MOD)) {
            System.err.println(cause.getMessage());
        } else {
            System.err.println(cause.getMessage());
            System.exit(1);
        }
    }
}
