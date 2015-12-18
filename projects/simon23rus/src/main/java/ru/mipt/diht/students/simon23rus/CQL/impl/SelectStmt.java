package ru.mipt.diht.students.simon23rus.CQL.impl;

import javafx.util.Pair;
import ru.mipt.diht.students.simon23rus.CQL.implOfAggregators.Aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectStmt<T, R> {
   public boolean isDistinct;
   public boolean isUnion;
   public boolean isJoin;
    int maxRawsNeeded;
    Predicate<T> whereRestriction;
    Predicate<R> havingRestriction;
    Function<T, ?>[] currentFunctions;
    Function<T, ?>[] groupByExpressions;
    Class toReturn;
    Comparator<R>[] orderByComparators;
    BestComparatorEver<R> bestComparatorEver;
    List<R> oldData;
    List<T> currentData;
    Stream<R> toStream;

    @SafeVarargs
    public SelectStmt(List<T> elements, Class<R> returnClass, boolean isDistinct, Function<T, ?>... functions) {
        this.oldData = new ArrayList<>();
        this.currentData = elements;
        this.toReturn = returnClass;
        this.isDistinct = isDistinct;
        this.currentFunctions = functions;
        this.maxRawsNeeded = -1;
        this.isUnion = false;
        this.isJoin = false;
    }

    public SelectStmt(List<T> elements, boolean isDistinct, Function<T, ?> first, Function<T, ?> second) {
        this.oldData = new ArrayList<>();
        this.currentData = elements;
        this.toReturn = elements.get(0).getClass();
        this.isDistinct = isDistinct;
        this.currentFunctions = new Function[]{first, second};
        this.maxRawsNeeded = -1;
        this.isUnion = false;
        this.isJoin = true;
    }

    @SafeVarargs
    public SelectStmt(List<R> pastElements, List<T> elements, Class<R> returnClass,
                      boolean isDistinct, Function<T, ?>... functions) {
        this.currentData = elements;
        this.toReturn = returnClass;
        this.isDistinct = isDistinct;
        this.currentFunctions = functions;
        this.maxRawsNeeded = -1;
        this.isUnion = true;
        this.isJoin = false;
        this.oldData = pastElements;
    }

    public SelectStmt(List<R> pastElements, List<T> elements, boolean isDistinct, Function<T, ?> first,
                      Function<T, ?> second) {
        this.currentData = elements;
        this.toReturn = elements.get(0).getClass();
        this.isDistinct = isDistinct;
        this.currentFunctions = new Function[]{first, second};
        this.maxRawsNeeded = -1;
        this.isUnion = true;
        this.isJoin = true;
        this.oldData = pastElements;
    }

    public int getMaxRawsNeeded() {
        return maxRawsNeeded;
    }

    public Class getToReturn() {
        return toReturn;
    }

    public Function<T, ?>[] getCurrentFunctions() {
        return currentFunctions;
    }

    public List<T> getCurrentData() {
        return currentData;
    }



    public SelectStmt<T, R> where(Predicate<T> predicate) {
        this.whereRestriction = predicate;
        return this;
    }

    public Iterable<R> execute() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<R> execResult = new ArrayList<>();
        Class[] returnClasses = new Class[currentFunctions.length];
        Object[] arguments = new Object[currentFunctions.length];
        if(whereRestriction != null) {
            //nuzhno otfiltrovat' dannie;
            List<T> filteredData = currentData.stream()
                    .filter(whereRestriction::test)
                    .collect(Collectors.toList());
            currentData = filteredData;
        }
        if(groupByExpressions != null) {
            Map<Integer, Integer> mapped = new HashMap<>();
            List<List<T>> groupedElements = new ArrayList<>();
            List<Pair<T, Integer>> grouped = new ArrayList<>();
            String[] results = new String[groupByExpressions.length];
            currentData.stream().forEach(
                    element -> {
                        for (int i = 0; i < groupByExpressions.length; i++) {
                            results[i] = (String) groupByExpressions[i].apply(element);
                        }
                        if (!mapped.containsKey(Objects.hash(results))) {
                            mapped.put(Objects.hash(results), mapped.size());
                        }
                        grouped.add(new Pair(element, mapped.get(Objects.hash(results))));
                    }
            );
            //dlya togo chtoby kazhdomu elementu iz mapa sootvetstovalo otvetvleniye
            for (int i = 0; i < mapped.size(); i++) {
                groupedElements.add(new ArrayList<T>());
            }

            for (Pair<T, Integer> element : grouped) {
                groupedElements.get(element.getValue()).add(element.getKey());
            }
            for (List<T> group : groupedElements) {
                int counter = 0;
                for (Function thisFunction : this.currentFunctions) {
                    arguments[counter] = (thisFunction instanceof Aggregator) ? ((Aggregator) thisFunction).apply(group) : thisFunction.apply(group.get(0));
                    returnClasses[counter] = arguments[counter].getClass();
                    ++counter;
                }
                if(isJoin) {
                    Tuple newElement = new Tuple(arguments[0], arguments[1]);
                    execResult.add((R) newElement);
                }
                else {
                    R newElement = (R) toReturn.getConstructor(returnClasses).newInstance(arguments);
                    execResult.add(newElement);
                }
            }
        }


        else {

            for(T elem : currentData) {
                int counter = 0;
                for (Function thisFunction : this.currentFunctions) {
                    List<T> thisElement = new ArrayList<>();
                    thisElement.add(elem);
                    arguments[counter] = (thisFunction instanceof Aggregator) ? ((Aggregator) thisFunction).apply(thisElement) : thisFunction.apply(elem);
                    returnClasses[counter] = arguments[counter].getClass();
                    ++counter;
                }
                if(isJoin) {
                    Tuple newElement = new Tuple(arguments[0], arguments[1]);
                    execResult.add((R) newElement);
                }
                else {
                    R newElement = (R) toReturn.getConstructor(returnClasses).newInstance(arguments);
                    execResult.add(newElement);
                }
            }

        }

        if (havingRestriction != null) {
            List<R> filteredData = execResult.stream()
                    .filter(havingRestriction::test)
                    .collect(Collectors.toList());
            execResult = filteredData;
        }

        if (isDistinct) {
            System.out.println(execResult);
            Set<Integer> hashes = new HashSet<>();
            List<R> distincted = new ArrayList<>();
            for (R element : execResult) {
                if (!hashes.contains(element.toString().hashCode())) {
                    hashes.add(element.toString().hashCode());
                    distincted.add(element);
                }
            }
            execResult = distincted;
        }

        if (orderByComparators != null) {
            execResult.sort(bestComparatorEver);
        }

        if (maxRawsNeeded != -1) {
            if(maxRawsNeeded < execResult.size())
                execResult = execResult.subList(0, maxRawsNeeded);
        }

        System.out.println(execResult.size());

        if (isUnion) {
            oldData.addAll(execResult);
            execResult = oldData;
        }

        System.out.println(execResult.size());

        return execResult;
    }

        @SafeVarargs
        public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) {
            this.groupByExpressions = expressions;
            return this;
        }

        @SafeVarargs
        public final SelectStmt<T, R> orderBy(Comparator<R>... comparators) {
            this.orderByComparators = comparators;
            this.bestComparatorEver = new BestComparatorEver<R>(comparators);
            return this;
        }

    public SelectStmt<T, R> having(Predicate<R> condition) {
           this.havingRestriction = condition;
            return this;
        }

    public SelectStmt<T, R> limit(int amount) {
            maxRawsNeeded = amount;
            return this;
        }

        public UnionStmt<T, R> union() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
            this.isUnion = true;
            List<R> answer = (List<R>) this.execute();
            System.out.println(answer.size());
            if (isJoin) {
                return new UnionStmt<>(answer, true);
            } else {
                return new UnionStmt<>(answer);
            }
        }


}
