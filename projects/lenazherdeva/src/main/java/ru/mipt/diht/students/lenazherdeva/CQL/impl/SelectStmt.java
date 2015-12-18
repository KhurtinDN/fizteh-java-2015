package ru.mipt.diht.students.lenazherdeva.CQL.impl;

/*
 * Created by admin on 18.11.2015.
*/

import javafx.util.Pair;
import ru.mipt.diht.students.lenazherdeva.CQL.impl.aggregators.Aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelectStmt<T, R> {

    private boolean isDistinct;
    private Class returnClazz;
    private Function[] functions;
    private List<T> objects;
    private boolean isUnion;

    private List<R> pastElements; //элементы внизу таблицы

    public final List<R> getPastElements() {
        return pastElements;
    }

    public final boolean isDistinct() {
        return isDistinct;
    }
    public final Class getReturnClass() {
        return returnClazz;
    }
    public final Function[] getFunctions() {
        return functions;
    }

    public final List<T> getElements() {
        return objects;
    }
    public final int getNumberOfObjects() {
        return numberOfObjects;
    }

    public final boolean isUnion() {
        return isUnion;
    }


    private Predicate<T> whereCondition;
    private Comparator<R>[] comparators;
    private Predicate<R> havingCondition;
    private int numberOfObjects;
    private Function<T, ?>[] groupByConditions;
    private CQLComparator<R> cqlComparator;



    public class CQLComparator<K> implements Comparator<K> {
        private Comparator<K>[] comparators;
        @SafeVarargs
        public CQLComparator(Comparator<K>... comparatorss) {
            this.comparators = comparatorss;
        }

        @Override
        public final int compare(K firstItem, K secondItem) {
            for (Comparator<K> comparator : comparators) {
                if (comparator.compare(firstItem, secondItem) != 0) {
                    return comparator.compare(firstItem, secondItem);
                }
            }
            return 0;
        }
    }

    @SafeVarargs
    public SelectStmt(List<T> objectss, Class<R> returnClazzz, boolean isDistinctt, Function<T, ?>... functionss) {
        this.objects = new ArrayList<>();
        for (T object : objectss) {
            this.objects.add(object);
        }
        this.returnClazz = returnClazzz;
        this.isDistinct = isDistinctt;
        this.functions = functionss;
        this.numberOfObjects = -1;
        this.isUnion = false;
    }


    public final SelectStmt<T, R> where(Predicate<T> predicate) {
        this.whereCondition = predicate;
        return this;
    }


    public final Iterable<R> execute() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        List<R> result = new ArrayList<>();
        Object[] arguments = new Object[functions.length];
        Class[] returnClasses = new Class[functions.length];

        if (whereCondition != null) { //where
            List<T> filtered = new ArrayList<>();
            objects.stream().filter(whereCondition::test).forEach(filtered::add); //the lambda expression implements
            // the Predicate.test() method.
            objects = filtered;
        }

        if (groupByConditions != null) { // groupByConditions-массив условий
            Map<Integer, Integer> mapped = new HashMap<>();
            String[] results = new String[groupByConditions.length];
            List<Pair<T, Integer>> groupedList = new ArrayList<>();
            objects.stream().forEach(
                    object -> {
                        for (int i = 0; i < groupByConditions.length; i++) {
                            results[i] = (String) groupByConditions[i].apply(object);
                        }
                        if (!mapped.containsKey(Objects.hash(results))) {   //если ранее не было в мэпе
                            mapped.put(Objects.hash(results), mapped.size());
                        }
                        groupedList.add(new Pair(object, mapped.get(Objects.hash(results))));
                    }
            ); //группируем все элементы по группам
            List<List<T>> groupedElements = new ArrayList<>();
            for (int i = 0; i < mapped.size(); i++) {
                groupedElements.add(new ArrayList<T>());
            }

            for (Pair<T, Integer> element : groupedList) {
                groupedElements.get(element.getValue()).add(element.getKey());
            }
            for (List<T> group : groupedElements) {
                for (int i = 0; i < functions.length; i++) {
                    if (functions[i] instanceof Aggregator) { //от какого класса произошел объект
                        arguments[i] = ((Aggregator) functions[i]).apply(group);
                    } else {
                        arguments[i] = functions[i].apply(group.get(0));
                    }

                    returnClasses[i] = arguments[i].getClass();
                }
                R newElement = (R) returnClazz.getConstructor(returnClasses).newInstance(arguments);
                result.add(newElement);
            }
        } else {
            for (T element : this.objects) {
                for (int i = 0; i < functions.length; i++) {
                    arguments[i] = functions[i].apply(element);
                    if (functions[i] instanceof Aggregator) {
                        List<T> currArg = new ArrayList<>();
                        currArg.add(element);
                        arguments[i] = ((Aggregator) functions[i]).apply(currArg);
                    } else {
                        arguments[i] = functions[i].apply(element);
                    }
                    returnClasses[i] = arguments[i].getClass();
                }
            }
        }
        if (havingCondition != null) {
            List<R> filtered = new ArrayList<>();
            result.stream().filter(havingCondition::test).forEach(filtered::add);
            result = filtered;
        }

        if (isDistinct) {
            Set<Integer> hashes = new HashSet<>();
            List<Integer> flags = new ArrayList<>();
            for (R element : result) {
                if (!hashes.contains(element.toString().hashCode())) { //если ранее не встречался
                    flags.add(1);
                    hashes.add(element.toString().hashCode());
                } else {
                    flags.add(0);
                }
            }
            List<R> distincted = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                if (flags.get(i) == 1) {
                    distincted.add(result.get(i));
                }
            }
            result = distincted;
        }

        if (comparators != null) {
            result.sort(cqlComparator);
        }

        if (numberOfObjects != -1) {
            while (result.size() > numberOfObjects) {
                result.remove(result.size() - 1);
            }
        }
        return result;
    }

        @SafeVarargs
        public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) {
            this.groupByConditions = expressions;
            return this;
        }

        @SafeVarargs
        public final SelectStmt<T, R> orderBy(Comparator<R>... comparatorss) {
            this.comparators = comparatorss;
            this.cqlComparator = new CQLComparator<R>(comparatorss);
            return this;
        }

        public final SelectStmt<T, R> having(Predicate<R> condition) {
            this.havingCondition = condition;
            return this;
        }

        public final SelectStmt<T, R> limit(int amount) {
            this.numberOfObjects = amount;
            return this;
        }
}
