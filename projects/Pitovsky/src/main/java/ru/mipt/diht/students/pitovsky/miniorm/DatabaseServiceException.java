package ru.mipt.diht.students.pitovsky.miniorm;

public class DatabaseServiceException extends Exception {

    public DatabaseServiceException(String message) {
        super(message);
    }

    public DatabaseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
