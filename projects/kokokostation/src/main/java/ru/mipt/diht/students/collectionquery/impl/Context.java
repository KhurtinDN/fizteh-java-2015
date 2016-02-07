package ru.mipt.diht.students.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikhail on 02.02.16.
 */
public class Context<R> {
    private List<SelectStmt<?, R>> selectStmts = new ArrayList<>();

    void add(SelectStmt<?, R> selectStmt) {
        selectStmts.add(selectStmt);
    }

    Iterable<SelectStmt<?, R>> get() {
        return selectStmts;
    }
}
