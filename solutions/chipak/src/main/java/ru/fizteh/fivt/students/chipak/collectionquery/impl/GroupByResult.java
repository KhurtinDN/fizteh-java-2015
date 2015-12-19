package ru.fizteh.fivt.students.chipak.collectionquery.impl;

import java.util.function.Function;

public class GroupByResult<T> {
    private Object[] results;

    public GroupByResult(T element, Function<T, ?>[] functions) {
        results = new Object[functions.length];
        for (int i = 0; i < functions.length; ++i) {
            results[i] = functions[i].apply(element);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GroupByResult)) {
            return false;
        }
        GroupByResult anotherResult = (GroupByResult) obj;
        if (results.length != anotherResult.results.length) {
            return false;
        }
        for (int i = 0; i < results.length; ++i) {
            if (!results[i].equals(anotherResult.results[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int ans = 0;
        for (Object element : results) {
            ans += element.hashCode();
        }
        return ans;
    }
}