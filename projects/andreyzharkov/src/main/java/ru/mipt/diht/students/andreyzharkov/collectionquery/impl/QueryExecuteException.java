package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

/**
 * Created by Андрей on 14.12.2015.
 */
public class QueryExecuteException extends Exception {
    QueryExecuteException(String message, Throwable reason) {
        super(message, reason);
    }
}
