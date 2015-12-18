package ru.mipt.diht.students.lenazherdeva.CQL;
import java.util.Arrays;
import java.util.List;


/**
 * Created by admin on 17.11.2015.
*/


public class Sources {
    @SafeVarargs //annotated method or constructor does not perform potentially unsafe operations
    public static <T> List<T> list(T... items) {
        return Arrays.asList(items);
    }

}

