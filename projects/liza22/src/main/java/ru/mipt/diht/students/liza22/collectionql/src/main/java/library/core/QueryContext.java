package library.core;

import library.api.Query;
import library.core.model.GroupingCondition;
import library.core.model.SelectArgument;

import java.util.*;
import java.util.function.Predicate;

/**
 * The most important class, which contains all information about executed query.
 *
 * This context is piped from one operation to another and shared between all operations.
 * It plays the role of shared storage on the every step.
 *
 * @param <R> result type
 * @param <S> source element type
 */
public final class QueryContext<R, S> {
    /**
     * Source elements of query ("from" statement).
     */
    private List<S> source;
    /**
     * Result class in "select" statement.
     */
    private Class<R> resultClass;
    /**
     * Select arguments in "select" statement.
     */
    private List<SelectArgument<S>> selectArguments;
    /**
     * Whether distinct select requested or not.
     */
    private boolean distinct;
    /**
     * Predicate in "where" statement.
     */
    private Predicate<S> where;
    /**
     * Grouping conditions in "groupBy" statement.
     */
    private List<GroupingCondition<S>> groupingConditions;
    /**
     * Predicate in "having" statement.
     */
    private Predicate<R> having;
    /**
     * Ordered comparators in "orderBy" statement.
     */
    private Comparator<R>[] orderBy;
    /**
     * Limit of result rows.
     */
    private int limit;
    /**
     * Joined queries by "union" statement.
     */
    private LinkedList<Query<R, S>> unions;
    /**
     * Result of query execution.
     */
    private LinkedList<R> result;

    public List<S> getSource() {
        return source;
    }

    public void setSource(List<S> source1) {
        this.source = source1;
    }

    public Class<R> getResultClass() {
        return resultClass;
    }

    public void setResultClass(Class<R> resultClass1) {
        this.resultClass = resultClass1;
    }


    public List<SelectArgument<S>> getSelectArguments() {
        return selectArguments;
    }

    public void setSelectArguments(List<SelectArgument<S>> selectArguments1) {
        this.selectArguments = selectArguments1;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct1) {
        this.distinct = distinct1;
    }

    public Predicate<S> getWhere() {
        return where;
    }

    public void setWhere(Predicate<S> where1) {
        this.where = where1;
    }

    public List<GroupingCondition<S>> getGroupingConditions() {
        return groupingConditions;
    }

    public void setGroupingConditions(List<GroupingCondition<S>> groupingConditions1) {
        this.groupingConditions = groupingConditions1;
    }

    public Predicate<R> getHaving() {
        return having;
    }

    public void setHaving(Predicate<R> having1) {
        this.having = having1;
    }

    public Comparator<R>[] getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Comparator<R>[] orderBy1) {
        this.orderBy = orderBy1;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit1) {
        this.limit = limit1;
    }

    public List<Query<R, S>> getUnions() {
        return unions;
    }

    public void addUnion(Query<R, S> union) {
        if (unions == null) {
            this.unions = new LinkedList<>();
        }
        unions.add(union);
    }

    public List<R> getResult() {
        return result;
    }

    public void setResult(List<R> result1) {
        this.result = new LinkedList<>(result1);
    }

    public void addResult(List<R> result1) {
        if (this.result == null) {
            this.result = new LinkedList<>();
        }
        this.result.addAll(0, result1);
    }

    public boolean isGroupingQuery() {
        return groupingConditions != null;
    }
}
