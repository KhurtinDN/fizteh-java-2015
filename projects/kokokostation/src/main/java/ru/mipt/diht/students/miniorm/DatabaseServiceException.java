package ru.mipt.diht.students.miniorm;

/**
 * Created by mikhail on 29.01.16.
 */
public class DatabaseServiceException extends Exception {
    DatabaseServiceException() {
    }

    DatabaseServiceException(String message) {
        super(message);
    }
}
