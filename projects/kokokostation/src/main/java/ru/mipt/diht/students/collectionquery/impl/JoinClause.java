package ru.mipt.diht.students.collectionquery.impl;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Created by mikhail on 03.02.16.
 */
interface JoinClause<C, I, J> {
    C on(BiPredicate<I, J> condition);

    <K extends Comparable<?>> C on(
            Function<I, K> leftKey,
            Function<J, K> rightKey);
}
