package ru.mipt.diht.students.maxdankow.miniorm;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

public class Utils {

    public static String camelCaseToLowerCase(String text) {
        StringBuilder lowerCaseText = new StringBuilder("");

        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (isUpperCase(ch)) {
                lowerCaseText.append("_")
                        .append(toLowerCase(ch));
            } else {
                lowerCaseText.append(ch);
            }
        }
        return lowerCaseText.toString();
    }
}
