package ru.fizteh.fivt.students.chipak.collectionquery.impl;

import ru.fizteh.fivt.students.chipak.collectionquery.AggregationFunction;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class SelectStmt<T, R> implements Query<R> {
    private List<T> source;
    private boolean isDistinct;
    private boolean hasConvertingFunction;
    private Function<T, R> converter;
    private Function<T, ?>[] functions;
    private Class<R> rClass;
    private List<?> previousResult;

    @SafeVarargs
    public SelectStmt(List<?> previousResult, List<T> source, Class<R> rClass,
                      boolean isDistinct, Function<T, ?>... functions) {
        this.functions = functions;
        hasConvertingFunction = false;
        this.converter = null;
        this.isDistinct = isDistinct;
        this.source = source;
        this.rClass = rClass;
        this.previousResult = previousResult;
    }

    public SelectStmt(List<?> previousResult, List<T> source, boolean isDistinct,
                      Function<T, R> s) {
        this.source = source;
        hasConvertingFunction = true;
        this.isDistinct = isDistinct;
        this.converter = s;
        this.functions = null;
        this.previousResult = previousResult;
    }


    public WhereStmt<T, R> where(Predicate<T> predicate) {
        return new WhereStmt<>(source, predicate, isDistinct, hasConvertingFunction,
                converter, functions, rClass);
    }

    @Override
    public Iterable<R> execute() throws ReflectiveOperationException {
        return new WhereStmt<T, R>(source, element -> true, isDistinct,
                hasConvertingFunction, converter, functions, rClass).execute();

    }

    @Override
    public Stream<R> stream() throws ReflectiveOperationException {
        return StreamSupport.stream(execute().spliterator(), false);
    }


    public class WhereStmt<T, R> implements Query<R> {

        private List<T> source;
        private Predicate<T> wherePredicate;
        private Function<T, ?>[] groupByExpressions;
        private Comparator<R>[] orderByComparators;
        private Predicate<R> havingPredicate;
        private boolean isDistinct;
        private boolean hasConvertingFunction;
        private Function<T, R> converter;
        private Function<T, ?>[] functions;
        private Class<R> rClass;
        private int limit = -1;

        public WhereStmt(List<T> source, Predicate<T> wherePredicate, boolean isDistinct,
                         boolean hasConvertingFunction, Function<T, R> converter,
                         Function<T, ?>[] functions, Class<R> rClass) {
            this.source = source;
            this.wherePredicate = wherePredicate;
            this.isDistinct = isDistinct;
            this.hasConvertingFunction = hasConvertingFunction;
            this.converter = converter;
            this.functions = functions;
            this.rClass = rClass;
        }

        @SafeVarargs
        public final WhereStmt<T, R> groupBy(Function<T, ?>... expressions) {
            groupByExpressions = expressions;
            return this;
        }

        @SafeVarargs
        public final WhereStmt<T, R> orderBy(Comparator<R>... comparators) {
            orderByComparators = comparators;
            return this;
        }

        public WhereStmt<T, R> having(Predicate<R> condition) {
            havingPredicate = condition;
            return this;
        }

        public WhereStmt<T, R> limit(int amount) {
            if (amount < 0) {
                throw new IllegalArgumentException("limit < 0");
            }
            limit = amount;
            return this;
        }

        public UnionStmt<R> union() throws ReflectiveOperationException {
            return new UnionStmt<R>(StreamSupport.stream(execute().spliterator(), false)
                    .collect(Collectors.toList()));
        }

        @Override
        public Iterable<R> execute() throws ReflectiveOperationException {
            List<List<R>> result = new ArrayList<>();
            if (wherePredicate != null) {
                source = source.stream().filter(wherePredicate::test).collect(Collectors.toList());
            }
            if (groupByExpressions != null) {
                List<List<T>> groupedSource = new ArrayList<>();
                Map<GroupByResult<T>, Integer> mapOfResultWithNumber = new HashMap<>();
                Map<T, GroupByResult<T>> mapOfElementWithResult = new HashMap<>();
                for (T element : source) {
                    GroupByResult<T> resultForElement = new GroupByResult<>(element, groupByExpressions);
                    mapOfElementWithResult.put(element, resultForElement);
                    if (!mapOfResultWithNumber.containsKey(resultForElement)) {
                        mapOfResultWithNumber.put(resultForElement, mapOfResultWithNumber.size());
                    }
                }
                for (int i = 0; i < mapOfResultWithNumber.size(); ++i) {
                    groupedSource.add(new ArrayList<>());
                }
                for (T element : source) {
                    groupedSource.get(mapOfResultWithNumber.get(mapOfElementWithResult.get(element))).
                            add(element);
                }
                for (List<T> source : groupedSource) {
                    result.add(executeOnList(source));
                }
            } else {
                result.add(executeOnList(source));
            }
            if (havingPredicate != null) {
                List<List<R>> newResult = new ArrayList<>();
                for (List<R> element : result) {
                    newResult.add(element.stream().filter(havingPredicate::test).
                            collect(Collectors.toList()));
                }
                result = newResult;
            }
            if (orderByComparators != null) {
                for (List<R> list : result) {
                    list.sort(compileComparator(orderByComparators));
                }
            }
            List<R> ans = new ArrayList<>();
            for (List<R> element : result) {
                ans.addAll(element);
            }
            if (limit >= 0) {
                ans = ans.subList(0, Math.min(ans.size(), limit));
            }
            if (previousResult != null) {
                if (previousResult.size() > 0) {
                    if (rClass != null && previousResult.get(0).getClass() != rClass
                            || ans.size() > 0
                            && previousResult.get(0).getClass() != ans.get(0).getClass()) {
                        throw new IllegalArgumentException("Can't call union");
                    } else {
                        List<R> newAns = (List<R>) previousResult;
                        newAns.addAll(ans);
                        ans = newAns;
                    }
                }
            }

            return ans;
        }

        private Comparator<R> compileComparator(Comparator<R>[] orderByComparators) {
            return (first, second) -> {
                for (int i = 0; i < orderByComparators.length - 1; ++i) {
                    if (orderByComparators[i].compare(first, second) != 0) {
                        return orderByComparators[i].compare(first, second);
                    }
                }
                return orderByComparators[orderByComparators.length - 1].compare(first, second);
            };
        }

        @Override
        public Stream<R> stream() throws ReflectiveOperationException {
            return StreamSupport.stream(execute().spliterator(), false);
        }

        private List<R> executeOnList(List<T> currentSource) throws ReflectiveOperationException {
            List<R> result = new ArrayList<>();
            if (hasConvertingFunction) {
                for (T element : currentSource) {
                    result.add(converter.apply(element));
                }
            } else {
                List<List<Object>> temporaryResults;
                temporaryResults = new ArrayList<>();
                for (int i = 0; i < currentSource.size(); ++i) {
                    temporaryResults.add(new ArrayList<>(functions.length));
                }
                for (Function<T, ?> function : functions) {
                    if (function instanceof AggregationFunction) {
                        Object resultOfFunction = ((AggregationFunction) function).apply(currentSource);
                        for (int j = 0; j < currentSource.size(); ++j) {
                            temporaryResults.get(j).add(resultOfFunction);
                        }
                    } else {
                        for (int j = 0; j < currentSource.size(); ++j) {
                            temporaryResults.get(j).add(function.apply(currentSource.get(j)));
                        }
                    }
                }
                for (List<Object> objects : temporaryResults) {
                    Class<?>[] classes = new Class[functions.length];
                    for (int i = 0; i < functions.length; ++i) {
                        classes[i] = temporaryResults.get(0).get(i).getClass();
                    }
                    Constructor<R> constructor = rClass.getConstructor(classes);
                    result.add(constructor.newInstance(objects.toArray()));
                }
            }
            if (isDistinct) {
                result = getDistinctResult(result);
            }
            return result;
        }

        private List<R> getDistinctResult(List<R> result) {
            Set<R> uniqueElements = new HashSet<>();
            List<R> uniqueResults = new ArrayList<>();
            for (R element : result) {
                if (!uniqueElements.contains(element)) {
                    uniqueResults.add(element);
                    uniqueElements.add(element);
                }
            }
            return uniqueResults;
        }
    }
}