package ru.mipt.diht.students.maxDankow.sqlcollections;

import java.util.Collection;
import java.util.stream.Stream;

public class CollectionQuery {
    Stream stream;

    public CollectionQuery(Stream stream) {
        this.stream = stream;
    }

    CollectionQuery from(Collection collection) {
        return new CollectionQuery(collection.stream());
    }
}
