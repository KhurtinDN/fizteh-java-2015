package ru.mipt.diht.students.maxDankow.sqlcollections;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CollectionQueryTests {
    @Test
    public void generalTests() {
        CollectionQuery query = new CollectionQuery();
        List<String> src = Arrays.asList("a", "b", "bac", "abba");
//        List<String> list = query.from(src).execute();
    }
}
