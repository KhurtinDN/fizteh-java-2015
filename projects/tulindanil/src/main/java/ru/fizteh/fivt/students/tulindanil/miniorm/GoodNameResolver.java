package ru.fizteh.fivt.students.tulindanil.miniorm;

/**
 * Created by tulindanil on 15.12.15.
 */
class GoodNameResolver {
    static final String REGEX = "[A-Za-z0-9_-]*";
    public static Boolean isGood(String name) {
        return name.matches(REGEX);
    }
}
