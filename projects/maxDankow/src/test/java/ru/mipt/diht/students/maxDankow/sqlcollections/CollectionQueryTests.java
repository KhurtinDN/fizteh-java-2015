package ru.mipt.diht.students.maxDankow.sqlcollections;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import static ru.mipt.diht.students.maxDankow.sqlcollections.Aggregates.avg;
import static ru.mipt.diht.students.maxDankow.sqlcollections.Aggregates.count;
import static ru.mipt.diht.students.maxDankow.sqlcollections.Conditions.rlike;
import static ru.mipt.diht.students.maxDankow.sqlcollections.OrderByConditions.asc;
import static ru.mipt.diht.students.maxDankow.sqlcollections.OrderByConditions.desc;
import static ru.mipt.diht.students.maxDankow.sqlcollections.Sources.list;
import static ru.mipt.diht.students.maxDankow.sqlcollections.statements.FromStatement.from;

@SuppressWarnings("all")
public class CollectionQueryTests {
    @Test
    public void generalTest() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Iterable<Statistics> statistics =
                from(list(
                        new Student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        new Student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        new Student("smith", LocalDate.parse("1986-08-06"), "495"),
                        new Student("petrov", LocalDate.parse("2006-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getName)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(count(Statistics::getCount)))
                        .limit(100)
                        .union()
                        .from(list(new Student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, s -> {
                            return "all";
                        }, count(s -> 1), avg(Student::age))
                        .execute();
        System.out.println(statistics);
    }
}
