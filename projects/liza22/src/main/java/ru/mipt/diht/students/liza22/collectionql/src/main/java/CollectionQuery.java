import client.Statistics;
import client.Student;
import library.core.exceptions.IncorrectQueryException;

import java.time.LocalDate;

import static client.Student.student;
import static library.api.Aggregates.avg;
import static library.api.Aggregates.constant;
import static library.api.Aggregates.count;
import static library.api.Conditions.like;
import static library.api.OrderByConditions.asc;
import static library.api.OrderByConditions.desc;
import static library.api.Sources.from;
import static library.api.Sources.list;

public class CollectionQuery {

    public static void main(String[] args) throws IncorrectQueryException {
        final int const10 = 10;
        final int const100 = 100;
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(like(Student::getName, ".*ov").and(s -> s.age() > const10))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(const100)
                        .union(
                                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                                        .selectDistinct(Statistics.class, constant("all"), count(s -> 1),
                                                avg(Student::age))
                        )
                        .execute();
        System.out.println(statistics);
    }

}
