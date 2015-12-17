package ru.mipt.diht.students.maxDankow.sqlcollections.statements;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class JoinStatement<L, R> {
    private List<L> leftList = new ArrayList<>();
    private List<R> rightList = new ArrayList<>();
    private List<Pair<L, R>> elements = new ArrayList<>();

    public JoinStatement(List<L> newLeftList, Iterable<R> newRightIterable) {
        leftList.addAll(newLeftList);
        for (R element : newRightIterable) {
            rightList.add(element);
        }
    }

    final FromStatement<Pair<L, R>> on(BiPredicate<L, R> condition) {
        for (L left : leftList) {
            for (R right : rightList) {
                if (condition.test(left, right)) {
                    elements.add(new Pair<>(left, right));
                }
            }
        }
        return new FromStatement<>(elements);
    }
}
