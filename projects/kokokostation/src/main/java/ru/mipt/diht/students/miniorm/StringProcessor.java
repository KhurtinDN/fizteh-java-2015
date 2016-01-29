package ru.mipt.diht.students.miniorm;

/**
 * Created by mikhail on 29.01.16.
 */
public class StringProcessor {
    public static String fromCamelCaseToLowerUnderscore(String string) {
        final String regex = "([a-z])([A-Z]+)";
        final String replacement = "$1_$2";
        return string.replaceAll(regex, replacement).toLowerCase();
    }

    public static void erase2LastLetters(String string) {
        string = string.substring(0, string.length() - 2);
    }
}