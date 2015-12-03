package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
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
    private Function<T, ?>[] convertFunctions;

    private Function<T, Comparable<?>>[] groupingFunctions;
    private Predicate<R> groupingCondition;

    private boolean isDistinct;

    @SafeVarargs
    public SelectStmt(Iterable<T> newBaseCollection, Class<R> clazz, boolean distinct, Function<T, ?>... s) {
        baseCollection = newBaseCollection;
        stream = StreamSupport.stream(baseCollection.spliterator(), false);
        outputClass = clazz;
        convertFunctions = s;
        isDistinct = distinct;
        groupingFunctions = null;
    }

    public final WhereStmt<T, R> where(Predicate<T> predicate) {
        return new WhereStmt<>(this, predicate);
    }

    final void setGroupingFunctions(Function<T, Comparable<?>>[] expressions) {
        groupingFunctions = expressions;
    }

    final void setGroupingCondition(Predicate<R> condition) {
        groupingCondition = condition;
    }

    class Applier implements Consumer<T> {
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private Constructor<R> getAskedConstructor() throws NoSuchMethodException, SecurityException {
        Class<?>[] outputParametrsTypes = new Class<?>[convertFunctions.length];
        for (int i = 0; i < convertFunctions.length; ++i) {
            //FIXME: find return class of function in NORMAL way
            outputParametrsTypes[i] = convertFunctions[i].apply(baseCollection.iterator().next()).getClass();
            //System.err.println(outputParametrsTypes[i]);
        }
        return outputClass.getConstructor(outputParametrsTypes);
    }

    private Collection<FinalRow<T, R>> goodGroups(Collection<FinalRow<T, R>> table) {
        List<FinalRow<T, R>> output = new ArrayList<>();
        for (FinalRow<T, R> row : table) {
            if (groupingCondition.test(row.get())) {
                output.add(row);
            }
        }
        return output;
    }

    private void aggregatingGroups(Collection<FinalRow<T, R>> table) throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
            row.updateRow(constructor.newInstance(parametrs));
        }
    }

    private Collection<R> convertToFinal(Collection<FinalRow<T, R>> table) {
        List<R> output = new ArrayList<>();
        for (FinalRow<T, R> row : table) {
            output.add(row.get());
        }
        return output;
    }

    public final Collection<R> execute() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Collection<FinalRow<T, R>> output = null;
        if (isDistinct) {
            output = new HashSet<>();
        } else {
            output = new ArrayList<>();
        }

        stream.forEach(new Applier(output, getAskedConstructor()));

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
            Comparator<FinalRow<T, R>> groupsComparator = WhereStmt.getCombinedComparator(resultComparators);
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
        return convertToFinal(output);
    }

    public final Stream<R> stream() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return execute().stream();
    }

    final Stream<T> currentStream() {
        return stream;
    }

    final void updateStream(Stream<T> newStream) {
        stream = newStream;
    }

}
