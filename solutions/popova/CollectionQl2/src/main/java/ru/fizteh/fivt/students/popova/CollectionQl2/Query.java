package ru.fizteh.fivt.students.popova.CollectionQl2;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by V on 19.12.2015.
 */
    public class Query<T, ResultType> {
        public Query(AbstractCollection<T> collection)
        {
            result = new ArrayList<>();
            actionsForInput = new ArrayList<Runnable>();
            actionsForOutput = new ArrayList<Runnable>();
            selectAction = null;
            sequence = collection.stream();
            selectResult = null;
        }

    /*private Query(AbstractCollection<T> collection, ArrayList<Query<?> > other)
    {

    }*/

        public Query<T,ResultType> where(Predicate<T> predicate)
        {
            actionsForInput.add(() -> {
                whereImpl(predicate);
            });
            return this;
        }

        public Query<T, ResultType> select(Class<ResultType> resultType, Function<T, ?>... functions)
        {
            selectAction = () -> {
                try {
                    selectImpl(resultType, functions);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
            return this;
        }


        private void selectImpl(Class<ResultType> resultType, Function<T, ?>... functions) throws InstantiationException
        {
            //ArrayList<Object> selected = new ArrayList<>();
            Iterator<T> it = sequence.iterator();
            //ArrayList<ResultType> result = new ArrayList<>();
            while(it.hasNext())
            {
                ArrayList<Object> selected = new ArrayList<>();
                for(Function<T, ?> f : functions)
                {
                    selected.add(f.apply(it.next()));
                }
                Constructor<?>[] constructors = resultType.getConstructors();
                ResultType t = null;
                for(int i = 0; i < constructors.length; ++i)
                {
                    if( t!= null) break;
                    try {
                        t = (ResultType) constructors[i].newInstance(selected.toArray());
                    } catch (Exception ex)
                    {
                    }
                }
                if( t== null) throw new InstantiationException();
                result.add(t);
            }
            resultTypeStream = result.stream();
            selectResult = result;
        }

        public Query<T, ResultType> orderBy(Comparator<T>... comparators)
        {
            actionsForInput.add(() -> {
                orderByImpl(comparators);
            });
            return this;
        }

        public Query<T, ResultType> groupBy(Function<T, ?>... functors)
        {
            actionsForInput.add(() -> groupByImpl(functors));
            return this;
        }

        //на самом деле должно быть Iterable<Statistics>
        public<ResultType> Iterable<ResultType> execute()
        {
            for( Runnable r : actionsForInput)
            {
                r.run();
            }
            selectAction.run();
            for(Runnable r1 : actionsForOutput){
                r1.run();
            }

            return (Iterable<ResultType>) selectResult;
        }

        private void whereImpl(Predicate<T> predicate)
        {
            sequence = sequence.filter(predicate).collect(Collectors.toList()).stream();
        }

        private void orderByImpl(Comparator<T>... comparators)
        {
            for(Comparator<T> comparator : comparators)
            {
                sequence = sequence.sorted().collect(Collectors.toList()).stream();
            }
        }

        private void groupByImpl(Function<T, ?>... functors)
        {
            for(Function<T, ?> f : functors)
            {
                ArrayList<T> filtered = new ArrayList<>();
                Map<T, List<T>> grouped = (Map<T, List<T>>) sequence.collect(Collectors.groupingBy(f));
                //apply aggreagate smth like:
                //grouped.forEach(aggregate);
                grouped.forEach((T key, List<T> value) -> filtered.add(value.get(0)));
                sequence = filtered.stream();
            }
        }

        public Query<T,ResultType> having(Predicate<ResultType> predicate){
            actionsForOutput.add(()->{
                havingImpl(predicate);
            });
            return this;
        }

        public void havingImpl(Predicate<ResultType> predicate){
            resultTypeStream= resultTypeStream.
                    filter(s -> predicate.test(s))
                    .collect(Collectors.toList())
                    .stream();
        }

        public Query<T,ResultType> selectDistinct(){
            actionsForOutput.add(()->{
                selectDistinctImpl();
            });
            return this;
        }

        public void selectDistinctImpl(){
            resultTypeStream = resultTypeStream
                    .distinct()
                    .collect(Collectors.toList())
                    .stream();
        }

        public Query<T, ResultType> limit(int n){
            actionsForOutput.add(()->{
                limitImpl(n);
            });
            return this;
        }

        public void limitImpl(int count){
            if(resultTypeStream.count() > count){
                resultTypeStream=resultTypeStream.limit(count);
            }
        }

        private Stream<ResultType> resultTypeStream;
        private ArrayList<ResultType> result;
        private ArrayList<Runnable> actionsForInput;
        private Runnable selectAction;
        private ArrayList<Runnable> actionsForOutput;
        private Stream<T> sequence;
        private Object selectResult;
    }

