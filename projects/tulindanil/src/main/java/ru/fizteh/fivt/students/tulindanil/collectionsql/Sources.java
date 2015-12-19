package ru.fizteh.fivt.students.tulindanil.collectionsql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tulindanil on 20.10.15.
 */

public class Sources {
    public static <E> List<E> list(E... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

}
