package CollectionQL;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by V on 05.12.2015.
 */
public class Selector<T, R> {
   /* static public List<Statistics> union(List<Student> students, List<Function<Student,Object>> Selected, Predicate<Student> Where,Function<Student,Object> GroupingBy,Predicate<Object> have) {
        List<Object> result1 = new ArrayList<>();
        CollectionsQL qquery = new CollectionsQL() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
        Stream<Student> pupil = students.stream();
        Stream<Student> pupil1 = qquery.where(pupil, Where);
        pupil = qquery.groupBy(pupil1, GroupingBy);
        for(Function<Student, Object> func: Selected){
           qquery.select(func, pupil).stream()
                                        .forEach(p->result1.add(p));
        }
        List<Statistics> result2 = new ArrayList<Statistics>();

    }*/
   List<Function<T, R>> functions_select;
   List<Function<T, ?>> functions_group;
    Predicate<T> predicate_where;
    Predicate<R> predicate_having;
    List<Comparator<T>> all_comparators;
    int limited;

    public void set_functions(Function<T, ?>... s){
        functions_group.addAll(Arrays.asList(s));
    }

    public void setFunctions_select(Function<T, R>... s){
        functions_select.addAll(Arrays.asList(s));
    }

    public void set_where(Predicate<T> p){
        predicate_where = p;
    }

    public void set_having(Predicate<R> p){
        predicate_having = p;
    }

    public void setComparators(Comparator<T>... comparators){
        all_comparators.addAll(Arrays.asList(comparators));
    }

    public void setLimited(int n){
        limited = n;
    }

}
