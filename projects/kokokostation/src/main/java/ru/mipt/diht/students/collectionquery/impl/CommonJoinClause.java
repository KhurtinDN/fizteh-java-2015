package ru.mipt.diht.students.collectionquery.impl;

import javafx.util.Pair;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by mikhail on 02.02.16.
 */
public abstract class CommonJoinClause<I, J> {
    List<I> left;
    List<J> right;

    public CommonJoinClause(List<I> left, List<J> right) {
        this.left = left;
        this.right = right;
    }

    public final Stream<Pair<I, J>> commonOn(BiPredicate<I, J> condition) {
        List<Pair<I, J>> result = new ArrayList<>();

        for (I tItem : left) {
            for (J jItem : right) {
                if (condition.test(tItem, jItem)) {
                    result.add(new Pair<>(tItem, jItem));
                }
            }
        }

        return result.stream();
    }

    public final <K extends Comparable<?>> Stream<Pair<I, J>> commonOn(
            Function<I, K> leftKey,
            Function<J, K> rightKey) {
        List<Pair<I, J>> result = new ArrayList<>();

        Map<K, List<I>> hashMap = new HashMap<>();

        for (I item : left) {
            K key = leftKey.apply(item);
            if (hashMap.containsKey(key)) {
                hashMap.get(key).add(item);
            } else {
                hashMap.put(key, Arrays.asList(item));
            }
        }

        for (J jItem : right) {
            for (I tItem : hashMap.get(rightKey.apply(jItem))) {
                result.add(new Pair<>(tItem, jItem));
            }
        }

        return result.stream();
    }

    public abstract Object on(BiPredicate<I, J> condition);
    public abstract <K extends Comparable<?>> Object on(
            Function<I, K> leftKey,
            Function<J, K> rightKey);
}
