package ru.mipt.diht.students.collectionquery.impl;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by mikhail on 02.02.16.
 */
public class CommonJoinClause<I, J> {
    private final List<I> left;
    private final List<J> right;

    public CommonJoinClause(List<I> left, List<J> right) {
        this.left = left;
        this.right = right;
    }

    CommonJoinClause(CommonJoinClause<I, J> common) {
        this(common.left, common.right);
    }

    public Stream<Pair<I, J>> onImpl(BiPredicate<I, J> condition) {
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

    public <K extends Comparable<?>> Stream<Pair<I, J>> onImpl(
            Function<I, K> leftKey,
            Function<J, K> rightKey) {
        List<Pair<I, J>> result = new ArrayList<>();

        Map<K, List<I>> hashMap = new HashMap<>();

        for (I item : left) {
            K key = leftKey.apply(item);
            if (hashMap.containsKey(key)) {
                hashMap.get(key).add(item);
            } else {
                hashMap.put(key, Utils.arrayListFromElement(item));
            }
        }

        for (J jItem : right) {
            K key = rightKey.apply(jItem);

            if (hashMap.containsKey(key)) {
                for (I tItem : hashMap.get(key)) {
                    result.add(new Pair<>(tItem, jItem));
                }
            }
        }

        return result.stream();
    }
}
