package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import javafx.util.Pair;
import ru.mipt.diht.students.andreyzharkov.collectionquery.Aggregates;

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
    private Iterable<R> previous;
    private Function<T, ?>[] convertFunctions;
    private Class<R> returnedClass;
    private Function<T, Comparable<?>>[] groupingFunctions;
    private Predicate<R> groupingCondition;
    private Comparator<T> resultComparator;
    private T example; //for initialization of out output class constructor arguments
    private boolean isDistinct;
    private boolean isTupleR;
    private long maxResultSize = -1;

    private Object[] constructorArguments;
    private Class[] resultClasses;

    WhereStmt(Iterable<R> prev, Iterable<T> iterable, Class<R> clazz, Predicate<T> predicate,
              boolean isDistnct, boolean isTuplR, Function<T, ?>[] convFunctions) {
        stream = StreamSupport.stream(iterable.spliterator(), false).filter(predicate);
        returnedClass = clazz;
        this.previous = prev;
        this.convertFunctions = convFunctions;
        this.isDistinct = isDistnct;
        this.isTupleR = isTuplR;
        groupingCondition = null;
        example = iterable.iterator().next();
    }

    @SafeVarargs
    public final WhereStmt<T, R> groupBy(Function<T, Comparable<?>>... expressions) {
        groupingFunctions = expressions;
        return this;
    }

    @SafeVarargs
    public final WhereStmt<T, R> orderBy(Comparator<T>... comparators) {
        resultComparator = getCombinedComparator(Arrays.asList(comparators));
        return this;
    }

    public final WhereStmt<T, R> having(Predicate<R> condition) {
        groupingCondition = condition;
        return this;
    }

    public final WhereStmt<T, R> limit(int amount) {
        maxResultSize = amount;
        return this;
    }

    public final UnionStmt<R> union() throws UnequalUnionClassesException,
            QueryExecuteException, EmptyCollectionException {
        return new UnionStmt<>(this.execute());
    }

    @Override
    public final Iterable<R> execute() throws QueryExecuteException, EmptyCollectionException {
        return this.stream().collect(Collectors.toList());
    }

    @Override
    public final Stream<R> stream() throws QueryExecuteException, EmptyCollectionException {
        constructorArguments = new Object[convertFunctions.length];
        resultClasses = new Class[convertFunctions.length];
        //
        for (int i = 0; i < convertFunctions.length; i++) {
            resultClasses[i] = convertFunctions[i].apply(example).getClass();
        }

        ArrayList<R> result = new ArrayList<>();
        if (groupingFunctions != null) {
            Map<Integer, Integer> mapped = new HashMap<>();
            List<List<T>> groupedElements = new ArrayList<>();
            List<Pair<T, Integer>> grouped = new ArrayList<>();
            String[] results = new String[groupingFunctions.length];
            stream.forEach(
                    element -> {
                        for (int i = 0; i < groupingFunctions.length; i++) {
                            results[i] = (String) groupingFunctions[i].apply(element);
                        }
                        if (!mapped.containsKey(Objects.hash(results))) {
                            mapped.put(Objects.hash(results), mapped.size());
                        }
                        grouped.add(new Pair<>(element, mapped.get(Objects.hash(results))));
                    }
            );

            for (int i = 0; i < mapped.size(); i++) {
                groupedElements.add(new ArrayList<>());
            }

            for (Pair<T, Integer> element : grouped) {
                groupedElements.get(element.getValue()).add(element.getKey());
            }
            for (List<T> group : groupedElements) {
                for (int i = 0; i < convertFunctions.length; i++) {
                    if (convertFunctions[i] instanceof Aggregates.Agregator) {
                        constructorArguments[i] = ((Aggregates.Agregator) convertFunctions[i]).apply(group);
                    } else {
                        constructorArguments[i] = convertFunctions[i].apply(group.get(0));
                    }
                    resultClasses[i] = constructorArguments[i].getClass();
                }
                try {
                    if (isTupleR) {
                        result.add((R) new Tuple<>(constructorArguments[0], constructorArguments[1]));
                    } else {
                        result.add(returnedClass.getConstructor(resultClasses)
                                .newInstance(constructorArguments));
                    }
                } catch (Exception ex) {
                    throw new QueryExecuteException("Failed to construct output class!", ex);
                }
            }
        } else {
            //лямбда исключение не прокидывает
            if (resultComparator != null) {
                stream.sorted(resultComparator);
            }
            for (T element : stream.collect(Collectors.toList())) {
                addToList(result, element);
            }
        }

        if (previous != null) {
            previous.forEach(result::add);
        }

        Stream<R> finalResult;
        if (isDistinct) {
            finalResult = result.stream().distinct();
        } else {
            finalResult = result.stream();
        }
        if (maxResultSize != -1) {
            finalResult = finalResult.limit(maxResultSize);
        }
        if (groupingCondition != null) {
            finalResult = finalResult.filter(groupingCondition);
        }
        return finalResult;
    }

    private void addToList(List<R> list, T element) throws QueryExecuteException {
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
