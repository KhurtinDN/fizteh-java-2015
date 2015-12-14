package CollectionQL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by V on 30.11.2015.
 */
public interface CollectionsQL{
    public static <T>List<T> list(T ... items){
        return Arrays.asList(items);
    }

    public static <T>List<T> select(final Function<Student, T> method, Stream<Student> myTable  ){

            List<T> returnList = new ArrayList<T>();
            myTable.forEach(x -> returnList.add(method.apply(x)));
            return returnList;
    }

   public static Stream<Statistics> select(List<Function<Student, Statistics>> methods, Stream<Student> myTable) {
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
        List<Object> res = new ArrayList<Object>();
        List<Statistics> result1 = new ArrayList<Statistics>();
        for (Function<Student, Statistics> func : methods) {
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
        return result1.stream();
    }

    public static <T>void where( Stream<T> myTable, Predicate<T> p  ){
            List<T> returnList = new ArrayList<T>();
            myTable
                    .filter(s -> p.test(s));
    }

    public static <T extends Comparable<T> > void order_by(Stream<T> myTable, List<Comparator<T>> Array_of_comparators){
        Array_of_comparators.stream()
                .forEach(comp -> Collections.sort(myTable.collect(Collectors.toList()), comp));
    }

    public static <T> Stream<T>groupBy(Stream<Student> myTable, Function<Student, T> function){
        Set<T> mySet = myTable.
                map(s->function.apply(s))
                .collect(Collectors.toSet());
        return  mySet.stream();
    }

    public static <T> Stream<T>having(Stream<T> myTable, Predicate<T> p){
        List<T> result = myTable.
                filter(s->p.test(s))
                .collect(Collectors.toList());
        return result.stream();
    }

    public static <T,S> Stream<S>selectDistinct(Stream<T> myTable, Function<T,S> function) {
        Set<S> mySet = myTable.
                map(s->function.apply(s))
                .collect(Collectors.toSet());
        return  mySet.stream();
    }
}


