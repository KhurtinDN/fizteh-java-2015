package ru.mipt.diht.students.lenazherdeva.CQL.impl.aggregators;


import java.util.List;

/**
 * Created by admin on 17.11.2015.
**/

    public class CountFunction<T> implements Aggregator<T, Integer> {
        @Override
        public final Integer applyOnList(List<T> list) {
            return list.size();
        }

        @Override
        public final Integer apply(T t) {
            return null;
        }
    }



