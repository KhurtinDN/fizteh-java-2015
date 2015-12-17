package ru.mipt.diht.students.andreyzharkov.miniORM;

/**
 * Created by Андрей on 17.12.2015.
 */
public class DatabaseServiceException extends Exception {
    DatabaseServiceException(String message) {
        super(message);
    }

    DatabaseServiceException(String messahe, Throwable cause) {
        super(messahe, cause);
    }
}
