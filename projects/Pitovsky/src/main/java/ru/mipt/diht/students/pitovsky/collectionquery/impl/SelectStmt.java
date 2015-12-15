package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ru.mipt.diht.students.pitovsky.collectionquery.Aggregates.Aggregate;

public class SelectStmt<T, R> {

    private Stream<T> stream;
    private Iterable<T> baseCollection;
    private Class<R> outputClass;
    private Iterable<R> previousPart;
    private Function<T, ?>[] convertFunctions;

    private Function<T, Comparable<?>>[] groupingFunctions;
    private Predicate<R> groupingCondition = null;
    private Comparator<R> finalComparator = null;
    private int finalLimit = -1;

    private boolean isDistinct;

    @SafeVarargs
    SelectStmt(Iterable<T> newBaseCollection, Iterable<R> previousTable,
            Class<R> clazz, boolean distinct, Function<T, ?>... s) {
        baseCollection = newBaseCollection;
        previousPart = previousTable;
        stream = StreamSupport.stream(newBaseCollection.spliterator(), false);
        outputClass = clazz;
        convertFunctions = s;
        isDistinct = distinct;
        groupingFunctions = null;
    }

    final Class<R> getOutputClass() {
        return outputClass;
    }

    private static <T> Comparator<T> getCombinedComparator(Iterable<Comparator<T>> comparators) {
        return new Comparator<T>() {
            @Override
            public int compare(T first, T second) {
                for (Comparator<T> comparator : comparators) {
                    int result = comparator.compare(first, second);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

    /**
     * Select only rows, which is good for this predicate.
     * @param predicate
     * @return
     */
    public final WhereStmt<T, R> where(Predicate<T> predicate) {
        stream = stream.filter(predicate);
        return new WhereStmt<>(this);
    }

    @SafeVarargs
    public final void groupBy(Function<T, Comparable<?>>... expressions) throws CollectionQuerySyntaxException {
        if (groupingFunctions != null) {
            throw new CollectionQuerySyntaxException("you also group table in the query");
        }
        groupingFunctions = expressions;
    }

    public final void having(Predicate<R> condition) throws CollectionQuerySyntaxException {
        if (groupingCondition != null) {
            throw new CollectionQuerySyntaxException("you also set grouping conditions");
        }
        groupingCondition = condition;
    }

    private class Applier implements Consumer<T> {
        private Collection<FinalRow<T, R>> output;
        private Constructor<R> constructor;

        Applier(Collection<FinalRow<T, R>> outputCollection, Constructor<R> resultConstructor) {
            output = outputCollection;
            constructor = resultConstructor;
        }

        @Override
        public void accept(T element) {
            Object[] parametrs = new Object[convertFunctions.length];
            for (int i = 0; i < convertFunctions.length; ++i) {
                parametrs[i] = convertFunctions[i].apply(element);
            }
            try {
                output.add(new FinalRow<T, R>(constructor.newInstance(parametrs), element));
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("constructor", e);
            }
        }

    }

    private Constructor<R> getAskedConstructor() throws CollectionQueryExecuteException {
        Class<?>[] outputParametrsTypes = new Class<?>[convertFunctions.length];
        for (int i = 0; i < convertFunctions.length; ++i) {
            //FIXME: find return class of function in NORMAL way
            outputParametrsTypes[i] = convertFunctions[i].apply(baseCollection.iterator().next()).getClass();
            //System.err.println(outputParametrsTypes[i]);
        }
        try {
            return outputClass.getConstructor(outputParametrsTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new CollectionQueryExecuteException("Can not found constructor in output class", e);
        }
    }

    private Collection<FinalRow<T, R>> goodGroups(Collection<FinalRow<T, R>> table) {
        List<FinalRow<T, R>> output = new ArrayList<>();
        for (FinalRow<T, R> row : table) {
            if (groupingCondition == null || groupingCondition.test(row.get())) {
                output.add(row);
            }
        }
        return output;
    }

    private void aggregatingGroups(Collection<FinalRow<T, R>> table) throws CollectionQueryExecuteException {
        Constructor<R> constructor = getAskedConstructor();
        Object[] parametrs = new Object[convertFunctions.length];
        for (FinalRow<T, R> row : table) {
            for (int i = 0; i < convertFunctions.length; ++i) {
                if (convertFunctions[i] instanceof Aggregate) {
                    parametrs[i] = ((Aggregate<T, ?>) convertFunctions[i]).forGroup(row.getFrom());
                } else {
                    parametrs[i] = convertFunctions[i].apply(row.getAnyFrom());
                }
            }
            try {
                row.updateRow(constructor.newInstance(parametrs));
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new CollectionQueryExecuteException("Can not instantiate output class", e);
            }
        }
    }

    private Collection<R> convertToFinal(Collection<FinalRow<T, R>> table) {
        List<R> output = new ArrayList<>();
        if (previousPart != null) {
            for (R row : previousPart) {
                output.add(row);
            }
        }
        for (FinalRow<T, R> row : table) {
            output.add(row.get());
        }
        return output;
    }

    /**
     * Final operation. Apply all predicates for base collection.
     * @return Output collection, contains sql query results.
     * @throws CollectionQueryExecuteException
     */
    public final Collection<R> execute() throws CollectionQueryExecuteException {
        Collection<FinalRow<T, R>> output = null;
        output = new ArrayList<>();

        try {
            stream.forEach(new Applier(output, getAskedConstructor()));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("constructor")) {
                throw new CollectionQueryExecuteException("Unavailiable constructor", e.getCause());
            } else {
                throw e;
            }
        }

        if (groupingFunctions != null) {
            List<FinalRow<T, R>> preCalcSortedTable = new ArrayList<>();
            for (FinalRow<T, R> element : output) {
                preCalcSortedTable.add(element);
            }
            List<Comparator<FinalRow<T, R>>> resultComparators = new ArrayList<>();
            for (Function<T, Comparable<?>> function : groupingFunctions) {
                resultComparators.add((r1, r2) -> {
                    Comparable result1 = function.apply(r1.getAnyFrom()); //raw type because of forbidden cast
                    return result1.compareTo(function.apply(r2.getAnyFrom()));
                });
            }
            Comparator<FinalRow<T, R>> groupsComparator = getCombinedComparator(resultComparators);
            preCalcSortedTable.sort(groupsComparator);
            Collection<FinalRow<T, R>> groupedTable = new ArrayList<>();

            FinalRow<T, R> currentGroup = null;
            for (FinalRow<T, R> row : preCalcSortedTable) {
                if (currentGroup != null && groupsComparator.compare(row, currentGroup) == 0) {
                    currentGroup.getFrom().add(row.getAnyFrom());
                } else {
                    if (currentGroup != null) {
                        groupedTable.add(currentGroup);
                    }
                    currentGroup = row;
                }
            }
            groupedTable.add(currentGroup);

            aggregatingGroups(output);
            output = goodGroups(groupedTable);
        }
        Stream<R> finalOutput = convertToFinal(output).stream();
        if (finalLimit >= 0) {
            finalOutput = finalOutput.limit(finalLimit);
        }
        if (finalComparator != null) {
            finalOutput = finalOutput.sorted(finalComparator);
        }
        if (isDistinct) {
            finalOutput = finalOutput.distinct();
        }
        return finalOutput.collect(() -> new ArrayList<R>(), (l, e) -> l.add(e), (l1, l2) -> l1.addAll(l2));
    }

    public final void limit(int limit) throws CollectionQuerySyntaxException {
        if (limit >= 0) {
            throw new CollectionQuerySyntaxException("you also set limitation");
        }
        finalLimit = limit;
    }

    @SafeVarargs
    public final void orderBy(Comparator<R>... comparators) throws CollectionQuerySyntaxException {
        if (finalComparator != null) {
            throw new CollectionQuerySyntaxException("you also set ordering functions");
        }
        finalComparator = getCombinedComparator(Arrays.asList(comparators));
    }

    public final Stream<R> stream() throws CollectionQueryExecuteException {
        return execute().stream();
    }

    final Stream<T> currentStream() {
        return stream;
    }

    /**
     * Union result of this query with results of other query. Asked types must be equals.
     * @return
     * @throws CollectionQueryExecuteException
     */
    public final UnionStmt<R> union() throws CollectionQueryExecuteException {
        return new UnionStmt<R>(this);
    }

}
