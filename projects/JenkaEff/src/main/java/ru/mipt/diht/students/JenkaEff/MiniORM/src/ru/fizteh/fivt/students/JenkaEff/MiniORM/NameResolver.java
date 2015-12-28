package ru.fizteh.fivt.students.JenkaEff.MiniORM;

//import com.google.common.base.CaseFormat;

class NameResolver {
    static final String REGEX = "[A-Za-z0-9_-]*";
    public static Boolean isGood(String name) {
        return name.matches(REGEX);
    }
}