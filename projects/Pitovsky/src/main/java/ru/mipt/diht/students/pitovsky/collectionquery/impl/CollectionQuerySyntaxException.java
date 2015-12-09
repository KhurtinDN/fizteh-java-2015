package ru.mipt.diht.students.pitovsky.collectionquery.impl;

public class CollectionQuerySyntaxException extends Exception {

    CollectionQuerySyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    CollectionQuerySyntaxException(String message) {
        super(message);
    }
}
