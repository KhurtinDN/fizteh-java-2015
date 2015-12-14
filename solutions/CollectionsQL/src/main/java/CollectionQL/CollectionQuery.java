package CollectionQL;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by V on 29.11.2015.
 */

public class CollectionQuery {
    public static void main(String[] args) {
            List<Function> Selected = new ArrayList<>();
            Predicate<Student> Where;
            Function<Student,Object> GroupingBy;
            Predicate<Object> have;
            int lim;
        Iterable<Statistics> statistics =
                from(list(
                        Student.student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        Student.student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        Student.student("smith", LocalDate.parse("1986-08-06"), "495"),
                        Student.student("petrov", LocalDate.parse("2006-08-06"), "494"))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getName)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Student::getGroup), desc(count(Student::getGroup)))
                        .limit(100)
                        .union()
                        .from(list(Student.student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, "all", count(s -> 1), avg(Student::age))
                        .execute();

    }
}
