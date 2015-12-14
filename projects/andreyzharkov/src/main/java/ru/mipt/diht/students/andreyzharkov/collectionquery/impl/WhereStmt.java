package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Андрей on 08.12.2015.
 */
public class WhereStmt<T, R> implements Query<R> {
    private Stream<T> stream;
    private Function<T, ?>[] convertFunctions;
    private Class<R> returnedClass;
    private Function<T, Comparable<?>>[] groupingFunctions;
    private Predicate<R> groupingCondition;
    private T example;//for initialization of out output class constructor arguments
    private boolean isDistinct;

    Object[] constructorArguments;
    Class[] resultClasses;

    WhereStmt(Iterable<T> iterable, Class<R> clazz, Predicate<T> predicate,
              boolean isDistinct, Function<T, ?>[] convertFunctions) {
        stream = StreamSupport.stream(iterable.spliterator(), false).filter(predicate);
        returnedClass = clazz;
        this.convertFunctions = convertFunctions;
        this.isDistinct = isDistinct;
        groupingCondition = null;
    }

    @SafeVarargs
    public final WhereStmt<T, R> groupBy(Function<T, Comparable<?>>... expressions) {
        groupingFunctions = expressions;
        return this;
    }

    @SafeVarargs
    public final WhereStmt<T, R> orderBy(Comparator<T>... comparators) {
        stream.sorted(getCombinedComparator(Arrays.asList(comparators)));
        return this;
    }

    public WhereStmt<T, R> having(Predicate<R> condition) {
        groupingCondition = condition;
        return this;
    }

    public WhereStmt<T, R> limit(int amount) {
        stream.limit(amount);
        return this;
    }

    public UnionStmt union() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<R> execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<R> stream() throws  QueryExecuteException {
        constructorArguments = new Object[convertFunctions.length];
        resultClasses = new Class[convertFunctions.length];
        for (int i = 0; i < convertFunctions.length; i++) {
            resultClasses[i] = convertFunctions[i].apply(example).getClass();
        }

        ArrayList<R> result = new ArrayList<>();
        for(T element: stream.collect(Collectors.toList())){
            addToList(result, element);
        }
        return result.stream().filter(groupingCondition);
    }

    private void addToList(List<R> list, T element) throws  QueryExecuteException{
        for (int i = 0; i < convertFunctions.length; i++) {
            constructorArguments[i] = convertFunctions[i].apply(element);
        }
        try {
            list.add(returnedClass.getConstructor(resultClasses).newInstance(constructorArguments));
        } catch (Exception ex) {
            throw new QueryExecuteException("Failed to construct output class!", ex);
        }
    }

    private static <T> Comparator<T> getCombinedComparator(List<Comparator<T>> comparators) {
        return (o1, o2) -> {
            for (Comparator<T> comp : comparators) {
                int res = comp.compare(o1, o2);
                if (res != 0) {
                    return res;
                }
            }
            return 0;
        };
    }
}
