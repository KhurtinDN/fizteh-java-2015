package library;

import client.Statistics;
import client.Student;
import library.api.Conditions;
import library.api.Sources;
import library.core.exceptions.IncorrectQueryException;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Iterator;

import static client.Student.student;
import static library.api.Aggregates.*;
import static library.api.Conditions.like;
import static library.api.Conditions.not;
import static library.api.OrderByConditions.asc;
import static library.api.OrderByConditions.desc;
import static library.api.Sources.from;
import static library.api.Sources.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Main test class with many test samples.
 */
public class CorrectQueryTest {

    @Test
    public void test_TaskSampleQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(Conditions.like(Student::getName, ".*ov").and(s -> s.age() > 10))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .union(
                                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                                        .selectDistinct(Statistics.class, constant("all"), count(s -> 1), avg(Student::age))
                        )
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("all", 1L, 30L), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", 2L, 24L), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", 1L, 29L), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void test_SimpleSelectQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, s -> 1L, Student::age)
                        .orderBy(asc(Statistics::getGroup), asc(Statistics::getAge))
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", 1L, 19L), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", 1L, 29L), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", 1L, 29L), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", 1L, 29L), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void test_SimpleSelectWithWhereQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, s -> 1L, Student::age)
                        .where(not(Conditions.like(Student::getName, ".*ov")))
                        .orderBy(asc(Statistics::getGroup), asc(Statistics::getAge))
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", 1L, 29L), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void test_SimpleSelectWithLimitQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, s -> 1L, Student::age)
                        .orderBy(asc(Statistics::getGroup), asc(Statistics::getAge))
                        .limit(2)
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", 1L, 19L), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", 1L, 29L), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void test_SelectWithAggregateFunctionsWithoutGroupingQuery() throws IncorrectQueryException {
        // find student with min age
        Iterable<Statistics> minAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("min"), constant(1L), min(Student::age))
                        .execute();

        Iterator<Statistics> minAgeIterator = minAge.iterator();
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("min", 1L, 19L), minAgeIterator.next());
        assertFalse(minAgeIterator.hasNext());

        // find student with max age
        Iterable<Statistics> maxAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("max"), constant(1L), max(Student::age))
                        .execute();

        Iterator<Statistics> maxAgeIterator = maxAge.iterator();
        assertTrue(maxAgeIterator.hasNext());
        assertEquals(new Statistics("max", 1L, 69L), maxAgeIterator.next());
        assertFalse(maxAgeIterator.hasNext());

        // find student with average age of all students
        Iterable<Statistics> avgAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("average"), constant(1L), avg(Student::age))
                        .execute();

        Iterator<Statistics> avgAgeIterator = avgAge.iterator();
        assertTrue(avgAgeIterator.hasNext());
        assertEquals(new Statistics("average", 1L, 36L), avgAgeIterator.next());
        assertFalse(avgAgeIterator.hasNext());

        // count all students
        Iterable<Statistics> cnt =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("all"), count(s -> 1), avg(Student::age))
                        .execute();

        Iterator<Statistics> cntIterator = cnt.iterator();
        assertTrue(cntIterator.hasNext());
        assertEquals(new Statistics("all", 4L, 36L), cntIterator.next());
        assertFalse(cntIterator.hasNext());
    }

    @Test
    public void test_SelectWithAggregateFunctionsWithGroupingQuery() throws IncorrectQueryException {
        // find student with min age by groups
        Iterable<Statistics> minAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(s -> 1), min(Student::age))
                        .groupBy(Student::getGroup)
                        .orderBy(asc(Statistics::getGroup))
                        .execute();

        Iterator<Statistics> minAgeIterator = minAge.iterator();
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("494", 2L, 19L), minAgeIterator.next());
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("495", 2L, 29L), minAgeIterator.next());
        assertFalse(minAgeIterator.hasNext());
    }

    @Test
    public void test_SelectWithManyUnionsQuery() throws IncorrectQueryException {
        // find student with min age
        Iterable<Statistics> minAndMaxAndAvgAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("min"), constant(1L), min(Student::age))
                        .union(
                                from(list(
                                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                                        .select(Statistics.class, constant("max"), constant(1L), max(Student::age))
                        )
                        .union(
                                from(list(
                                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                                        .select(Statistics.class, constant("average"), constant(1L), avg(Student::age))
                        )
                        .execute();

        Iterator<Statistics> minAndMaxAndAvgAgeIterator = minAndMaxAndAvgAge.iterator();
        assertTrue(minAndMaxAndAvgAgeIterator.hasNext());
        assertEquals(new Statistics("average", 1L, 36L), minAndMaxAndAvgAgeIterator.next());
        assertTrue(minAndMaxAndAvgAgeIterator.hasNext());
        assertEquals(new Statistics("max", 1L, 69L), minAndMaxAndAvgAgeIterator.next());
        assertTrue(minAndMaxAndAvgAgeIterator.hasNext());
        assertEquals(new Statistics("min", 1L, 19L), minAndMaxAndAvgAgeIterator.next());
        assertFalse(minAndMaxAndAvgAgeIterator.hasNext());
    }

    @Test
    public void test_SelectWithAggregateFunctionsWithManyGroupingConditionsQuery() throws IncorrectQueryException {
        // group students by group and age and calculate their count
        Iterable<Statistics> minAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(s -> 1), Student::age)
                        .groupBy(Student::getGroup, Student::age)
                        .orderBy(asc(Statistics::getGroup), asc(Statistics::getAge))
                        .execute();

        Iterator<Statistics> minAgeIterator = minAge.iterator();
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("494", 1L, 19L), minAgeIterator.next());
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("494", 1L, 29L), minAgeIterator.next());
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("495", 2L, 29L), minAgeIterator.next());
        assertFalse(minAgeIterator.hasNext());
    }
}