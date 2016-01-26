package ru.mipt.diht.students.Reverser;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by mikhail on 15.12.15.
 */
public class Reverser {
    public static void main(String[] args) {
        String[] numbers = StringUtils.join(args, " ").split("\\s+");
        ArrayUtils.reverse(numbers);
        System.out.println(StringUtils.join(numbers, " "));
    }
}
