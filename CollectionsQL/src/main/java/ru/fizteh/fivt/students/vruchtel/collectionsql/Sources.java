package ru.fizteh.fivt.students.vruchtel.collectionsql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Серафима on 18.12.2015.
 */
public class Sources {
    public static <Type> List<Type> list(Type... data) {
        return new ArrayList<>(Arrays.asList(data));
    }

}