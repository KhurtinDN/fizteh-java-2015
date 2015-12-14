package CollectionsQL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by V on 05.12.2015.
 */
public class Selector {
    static public List<Statistics> union(List<Student> students, List<Function<Student,Object>> Selected, Predicate<Student> Where,Function<Student,Object> GroupingBy,Predicate<Object> have) {
        List<Object> result1 = new ArrayList<>();
        for(Function<Student, Object> func: Selected){
            select();
        }
    }
}
