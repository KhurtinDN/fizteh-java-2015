package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectStmt<T, R> implements Query<R> {
    private Iterable<T> data;
    private Function<T, ?>[] functions;
    private Class<R> returnedClass;
    private boolean isDistinct;

    @SafeVarargs
    SelectStmt(Iterable<T> data, Class<R> clazz, boolean isDistinct, Function<T, ?>... s) {
        this.data = data;
        functions = s;
        returnedClass = clazz;
        this.isDistinct = isDistinct;
    }

    SelectStmt(Iterable<T> data, boolean isDistinct, Function<T, R> func) {
        this.data = data;
        functions = new Function[]{func};
        returnedClass = (Class<R>) func.apply(data.iterator().next()).getClass();
        this.isDistinct = isDistinct;
    }

    <F, S> SelectStmt(Iterable<T> data, boolean isDistinct, Function<T, F> first, Function<T, S> second) {
        this.data = data;
        functions = new Function[]{first, second};
        returnedClass = (Class<R>) (new Tuple(first.apply(data.iterator().next()),
                second.apply(data.iterator().next()))).getClass();
        this.isDistinct = isDistinct;
    }

    public WhereStmt<T, R> where(Predicate<T> predicate) {
        return new WhereStmt<>(data, returnedClass, predicate, isDistinct, functions);
    }

    @Override
    public Iterable<R> execute() throws QueryExecuteException {
        if (data == null) {
            return null;
        }

        Object[] constructorArguments = new Object[functions.length];
        Class[] resultClasses = new Class[functions.length];
        for (int i = 0; i < functions.length; i++) {
            resultClasses[i] = functions[i].apply(data.iterator().next()).getClass();
        }

        ArrayList<R> result = new ArrayList<>();
        for (T element : data) {
            for (int i = 0; i < functions.length; i++) {
                constructorArguments[i] = functions[i].apply(element);
            }
            try {
                result.add(returnedClass.getConstructor(resultClasses).newInstance(constructorArguments));
            } catch (Exception ex) {
                throw new QueryExecuteException("Failed to construct output class!", ex);
            }
        }

        if (isDistinct) {
            return result.stream().distinct().collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public Stream<R> stream() throws QueryExecuteException {
        if (data == null) {
            return null;
        }

        Object[] constructorArguments = new Object[functions.length];
        Class[] resultClasses = new Class[functions.length];
        for (int i = 0; i < functions.length; i++) {
            resultClasses[i] = functions[i].apply(data.iterator().next()).getClass();
        }

        ArrayList<R> result = new ArrayList<>();
        for (T element : data) {
            for (int i = 0; i < functions.length; i++) {
                constructorArguments[i] = functions[i].apply(element);
            }
            try {
                result.add(returnedClass.getConstructor(resultClasses).newInstance(constructorArguments));
            } catch (Exception ex) {
                throw new QueryExecuteException("Failed to construct output class!", ex);
            }
        }

        if (isDistinct) {
            return result.stream().distinct();
        }
        return result.stream();
    }

}