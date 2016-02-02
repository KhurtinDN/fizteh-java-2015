package ru.mipt.diht.students.collectionquery.impl;

import java.util.List;

/**
 * Created by mikhail on 02.02.16.
 */
public interface JoinClauseFactory<C extends CommonJoinClause> {
    <I, J> C produce(List<I> left, List<J> right);
}
