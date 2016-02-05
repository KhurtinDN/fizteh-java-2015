package ru.mipt.diht.students.Reverser;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mikhail on 15.12.15.
 */
public class Reverser {
    public static void main(String[] args) {
        Arrays.stream(Arrays.asList(args).stream().reduce((a, b) -> a + " " + b).get().split("\\s+"))
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator().forEachRemaining(a -> System.out.print(a + " "));
        //p. s про то как ревёрсить не я придумал
    }
}
