package CollectionQL;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by V on 14.12.2015.
 */
public class Query {
    public Query()
    {
        actionsForInput = new ArrayList<Runnable>();
        actionsForOutput = new ArrayList<Runnable>();
        selectAction = null;
        ArrayList students = new ArrayList<Student>();
        students.add(new Student("ivanov", LocalDate.parse("1986-08-06"), "494"));
        students.add(new Student("sidorov", LocalDate.parse("1999-08-06"), "494"));
        students.add(new Student("john", LocalDate.parse("1987-08-06"), "494"));
        myTable = students.stream();
    }
    public Query where(Predicate<Student> predicate)
    {
        actionsForInput.add(() -> {
            whereImpl(predicate);
        });
        return this;
    }

    public<ResultType> Query select(Class<ResultType> resultType, Function<Student, Object>... functions)
    {
        selectAction = () -> {
            selectImpl(functions);
        };
        return this;
    }

    public void selectImpl( Function<Student, Object>... functions){
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
        List<Object> res = new ArrayList<Object>();
        List<Statistics> result1 = new ArrayList<Statistics>();
        for (Function<Student, Object> func : functions) {
            myTable
                    .map(p -> func.apply(p))
                    .forEach(s -> res.add(s));
            if (res.get(0).getClass().equals(Long.class)) {
                result.put("age", res);
            } else if (res.get(0).getClass().equals(Integer.class)) {
                result.put("count", res);
            } else {
                result.put("name", res);
            }
            for (int i = 0; i < myTable.toArray().length; ++i) {
                result1.add(new Statistics(null, 0, null));
            }
            for (Statistics st : result1) {
                for (String key : result.keySet()) {
                    if (key == "name") {
                        result.get(key).stream()
                                .forEach(p -> st.ChangeGroup(p.toString()));
                    } else if (key == "count") {
                        result.get(key).stream()
                                .map(p -> ((Number) p).intValue())
                                .forEach(p -> st.ChangeCount(p));
                    } else {
                        result.get(key).stream()
                                .map(p -> ((Number) p).longValue())
                                .forEach(p -> st.ChangeAge(p));
                    }
                }
            }
        }
        myStatistics = result1.stream();
    }

    /*
    public<ResultType> void selectImpl(Class<ResultType> resultType, Function<Student, Object>... functions)
    {
        превращаем input в output
    }
    */

    public Query orderBy(Comparator<Student>... comparators)
    {
        actionsForInput.add(() -> {
            orderByImpl(comparators);
        });
        return this;
    }

    public <Type> Query groupBy(Function<Student,Type > ... functions){
        actionsForInput.add(()->{
            groupByImpl(functions);
        });
        return this;
    }

    public <T> void groupByImpl(Function<Student, T> ... functions){
        /*Set<T> mySet;
        for(Function<Student, Object> func: functions ){
            mySet=myStatistics.
                    map(s->func.apply(s))
                    .collect(Collectors.toSet());
        }*/

    }

    public Query having(Predicate<Statistics> predicate){
        actionsForOutput.add(()->{
            havingImpl(predicate);
        });
        return this;
    }
    public void havingImpl(Predicate<Statistics> predicate){
        myStatistics = myStatistics.
                filter(s->predicate.test(s))
                .collect(Collectors.toList())
                .stream();
    }

    public Query limit(int n){
        actionsForOutput.add(()->{
            limitImpl(n);
        });
        return this;
    }

    public void limitImpl(int count){
        if(myStatistics.count() > count){
            myStatistics=myStatistics.limit(count);
        }
    }

    public Query selectDistinct(){
        actionsForOutput.add(()->{
            selectDistinctImpl();
        });
        return this;
    }

    public <T>void selectDistinctImpl(){
       myStatistics=myStatistics.collect(Collectors.toSet()).stream();
    }

    //на самом деле должно быть Iterable<Statistics>
    public Iterable<Statistics> execute()
    {
        for( Runnable r : actionsForInput)
        {
            r.run();
        }

        selectAction.run();

        for( Runnable r : actionsForOutput)
        {
            r.run();
        }

        return myStatistics.collect(Collectors.toList());
    }

    private void whereImpl(Predicate<Student> predicate)
    {
        myTable = myTable.filter(predicate);
    }

    private void orderByImpl(Comparator<Student>... comparators)
    {
        for(Comparator<Student> c : comparators)
        {
            myTable = myTable.sorted(c);
        }
    }

    private ArrayList<Runnable> actionsForInput;
    private Runnable selectAction;
    private ArrayList<Runnable> actionsForOutput;
    private Stream<Student> myTable;
    private Stream<Statistics> myStatistics;
}
