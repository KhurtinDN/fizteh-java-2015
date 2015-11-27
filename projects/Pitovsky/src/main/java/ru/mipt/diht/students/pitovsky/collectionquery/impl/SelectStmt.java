package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import ru.mipt.diht.students.pitovsky.collectionquery.impl.UnionStmt;
//import ru.mipt.diht.students.pitovsky.collectionquery.impl.WhereStmt;

public class SelectStmt<T, R> {

    private Iterable<T> base;
    private Class<R> outputClass;
    private Function<T, ?>[] convertFunctions;
    private boolean isDistinct;

    @SafeVarargs
    public SelectStmt(Iterable<T> baseCollection, Class<R> clazz, boolean distinct, Function<T, ?>... s) {
        base = baseCollection; //TODO: copy
        outputClass = clazz;
        convertFunctions = s;
        isDistinct = distinct;
    }

    public WhereStmt<T, R> where(Predicate<T> predicate) {
        return new WhereStmt<>(this, predicate);
    }

    public Iterable<R> execute() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Collection<R> output = null;
        if (isDistinct) {
            output = new HashSet<>();
        } else {
            output = new ArrayList<>();
        }

        Class<?>[] outputParametrsTypes = new Class<?>[convertFunctions.length];
        for (int i = 0; i < convertFunctions.length; ++i) {
            //FIXME: find return class of function in NORMAL way
            outputParametrsTypes[i] = convertFunctions[i].apply(base.iterator().next()).getClass();
            //System.err.println(outputParametrsTypes[i]);
        }
        Constructor<R> constructor = outputClass.getConstructor(outputParametrsTypes);
        for (T element : base) {
            Object[] parametrs = new Object[convertFunctions.length];
            for (int i = 0; i < convertFunctions.length; ++i) {
                parametrs[i] = convertFunctions[i].apply(element);
            }
            output.add(constructor.newInstance(parametrs)); //TODO: exceptions
        }
        return output;
    }

    public Stream<R> stream() {
        throw new UnsupportedOperationException();
    }

    public class WhereStmt<T, R> {
        private Predicate<T> currentPredicate;
        private SelectStmt<T, R> baseStmt;
        private List<T> currentElements;

        private WhereStmt(SelectStmt<T, R> selectStmt, Predicate<T> predicate) {
            currentPredicate = predicate;
            baseStmt = selectStmt;
            currentElements = new ArrayList<>();
            for (T element : baseStmt.base) {
                currentElements.add(element);
            }
        }

        @SafeVarargs
        public final WhereStmt<T, R> groupBy(Function<T, ?>... expressions) {
            applyPredicate();
            Set<List<Object>> groupingValues = new HashSet<>();
            for (T element : currentElements) {
                List<Object> result = new ArrayList<>();
                for (int i = 0; i < expressions.length; ++i) {
                    result.add(expressions[i].apply(element));
                }
                groupingValues.add(result);
            }
            //TODO: aggregates work, better asymptotic
            ArrayList<T> newElements = new ArrayList<>();
            for (List<Object> values : groupingValues) {
                for (T element : currentElements) {
                    boolean found = true;
                    for (int i = 0; i < expressions.length; ++i) {
                        if (!expressions[i].apply(element).equals(values.get(i))) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        //System.err.println("added " + element);
                        newElements.add(element);
                        break;
                    }
                }
            }
            currentElements = newElements;
            return this;
        }

        @SafeVarargs
        public final WhereStmt<T, R> orderBy(Comparator<T>... comparators) {
            Comparator<T> combinatedComparator = new Comparator<T>() {
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

            Collections.sort(currentElements, combinatedComparator);
            return this;
        }

        public WhereStmt<T, R> having(Predicate<R> condition) {
            throw new UnsupportedOperationException();
        }

        public WhereStmt<T, R> limit(int amount) {
            List<T> cuttedElements = new ArrayList<T>();
            for (int i = 0; i < amount && i < currentElements.size(); ++i) {
                cuttedElements.add(currentElements.get(i));
            }
            currentElements = cuttedElements;
            return this;
        }

        private void applyPredicate() {
            Iterator<T> i = currentElements.iterator();
            T element = null;
            while (i.hasNext()) {
                element = i.next();
                if (!currentPredicate.test(element)) {
                    i.remove();
                }
            }
        }

        public Iterable<R> execute() throws NoSuchMethodException, SecurityException, InstantiationException,
                IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            applyPredicate();
            baseStmt.base = currentElements;
            return baseStmt.execute();
        }

        public UnionStmt union() {
            throw new UnsupportedOperationException();
        }
    }

}
