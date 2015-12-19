package ru.fizteh.fivt.students.chipak.miniorm;

public class DatabaseServiceException extends Exception {

    public DatabaseServiceException(String message) {
        super(message);
    }

    public DatabaseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
