package library;

import client.Statistics;
import client.Student;
import library.api.Conditions;
import library.core.exceptions.IncorrectQueryException;
import org.junit.Test;

import java.time.LocalDate;

import static client.Student.student;
import static library.api.Aggregates.avg;
import static library.api.Aggregates.count;
import static library.api.OrderByConditions.asc;
import static library.api.OrderByConditions.desc;
import static library.api.Sources.from;
import static library.api.Sources.list;

/**
 * Test class contains cases with incorrect library usage.
 *
 * In most such cases library should generate IncorrectQueryException,
 * in other cases library generates IllegalArgumentException
 * or NullPointerException in case when required argument is null.
 */
public class IncorrectQueryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySourceQuery() throws IncorrectQueryException {
        from(list(/* empty */))
                .select(Statistics.class)
                .execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyListOfSelectArgs() throws IncorrectQueryException {
        from(list(new Object())).select(Object.class).execute();
    }

    @Test(expected = NullPointerException.class)
    public void testNullResultClass() throws IncorrectQueryException {
        from(list(new Object())).select(null).execute();
    }

    @Test(expected = NullPointerException.class)
    public void testNullWhereStatement() throws IncorrectQueryException {
        from(list(new Object())).select(Object.class, o -> 1).where(null).execute();
    }

    @Test(expected = NullPointerException.class)
    public void testNullOrderByStatement() throws IncorrectQueryException {
        from(list(new Object())).select(Object.class, o -> 1).orderBy(null).execute();
    }

    @Test(expected = NullPointerException.class)
    public void testNullGroupByStatement() throws IncorrectQueryException {
        from(list(new Object())).select(Object.class, o -> 1).groupBy(null).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_EmptyOrderByStatement() throws IncorrectQueryException {
        from(list(new Object())).select(Object.class, o -> 1).orderBy().execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyGroupByStatement() throws IncorrectQueryException {
        from(list(new Object())).select(Object.class, o -> 1).groupBy().execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectLimitStatement() throws IncorrectQueryException {
        from(list(new Object())).select(Object.class, o -> 1).limit(0).execute();
    }

    @Test(expected = IncorrectQueryException.class)
    public void testIncorrectSelectArguments() throws IncorrectQueryException {
        // class Statistics doesn't have constructor with 2 arguments
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup))
                        .where(Conditions.like(Student::getName, ".*ov").and(s -> s.age() > 10))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .execute();
    }

    @Test(expected = IncorrectQueryException.class)
    public void testAggregateAndNotAggregateFunctionsInNotGroupingQuery() throws IncorrectQueryException {
        // select statement contains Student::getGroup but there is no such grouping function
        // in such case the only aggregate functions or constant() are permitted
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(Conditions.like(Student::getName, ".*ov").and(s -> s.age() > 10))
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .execute();
    }
}