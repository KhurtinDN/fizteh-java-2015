package library;

import client.Statistics;
import client.Student;
import library.core.exceptions.IncorrectQueryException;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Iterator;

import static client.Student.student;
import static library.api.Aggregates.*;
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
public final class CorrectQueryTest {
    public static final long NUM1 = 1L;
    public static final long NUM2 = 2L;
    public static final long NUM4 = 4L;
    public static final long NUM19 = 19L;
    public static final long NUM29 = 29L;
    public static final long NUM36 = 36L;
    public static final long NUM69 = 69L;
    public static final long NUM24 = 24L;
    public static final long NUM30 = 30L;
    public static final long AGE = 10;
    public static final long LIMIT = 100;

    @Test
    public void testTaskSampleQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(Conditions.like(Student::getName, ".*ov").and(s -> s.age() > AGE))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(LIMIT)
                        .union(
                                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                                        .selectDistinct(Statistics.class, constant("all"), count(s -> 1),
                                                avg(Student::age))
                        )
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("all", NUM1, NUM30), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", NUM2, NUM24), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", NUM1, NUM29), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void testSimpleSelectQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, s -> NUM1, Student::age)
                        .orderBy(asc(Statistics::getGroup), asc(Statistics::getAge))
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", NUM1, NUM19), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", NUM1, NUM29), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", NUM1, NUM29), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", NUM1, NUM29), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void testSimpleSelectWithWhereQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, s -> NUM1, Student::age)
                        .where(not(Conditions.like(Student::getName, ".*ov")))
                        .orderBy(asc(Statistics::getGroup), asc(Statistics::getAge))
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("495", NUM1, NUM29), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void testSimpleSelectWithLimitQuery() throws IncorrectQueryException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, s -> NUM1, Student::age)
                        .orderBy(asc(Statistics::getGroup), asc(Statistics::getAge))
                        .limit(2)
                        .execute();

        Iterator<Statistics> resultIterator = statistics.iterator();
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", NUM1, NUM19), resultIterator.next());
        assertTrue(resultIterator.hasNext());
        assertEquals(new Statistics("494", NUM1, NUM29), resultIterator.next());
        assertFalse(resultIterator.hasNext());
    }

    @Test
    public void testSelectWithAggregateFunctionsWithoutGroupingQuery() throws IncorrectQueryException {
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
        assertEquals(new Statistics("min", NUM1, NUM19), minAgeIterator.next());
        assertFalse(minAgeIterator.hasNext());

        // find student with max age
        Iterable<Statistics> maxAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("max"), constant(NUM1), max(Student::age))
                        .execute();

        Iterator<Statistics> maxAgeIterator = maxAge.iterator();
        assertTrue(maxAgeIterator.hasNext());
        assertEquals(new Statistics("max", NUM1, NUM69), maxAgeIterator.next());
        assertFalse(maxAgeIterator.hasNext());

        // find student with average age of all students
        Iterable<Statistics> avgAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("average"), constant(NUM1), avg(Student::age))
                        .execute();

        Iterator<Statistics> avgAgeIterator = avgAge.iterator();
        assertTrue(avgAgeIterator.hasNext());
        assertEquals(new Statistics("average", NUM1, NUM36), avgAgeIterator.next());
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
        assertEquals(new Statistics("all", NUM4, NUM36), cntIterator.next());
        assertFalse(cntIterator.hasNext());
    }

    @Test
    public void testSelectWithAggregateFunctionsWithGroupingQuery() throws IncorrectQueryException {
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
        assertEquals(new Statistics("494", NUM2, NUM19), minAgeIterator.next());
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("495", NUM2, NUM29), minAgeIterator.next());
        assertFalse(minAgeIterator.hasNext());
    }

    @Test
    public void testSelectWithManyUnionsQuery() throws IncorrectQueryException {
        // find student with min age
        Iterable<Statistics> minAndMaxAndAvgAge =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                        .select(Statistics.class, constant("min"), constant(NUM1), min(Student::age))
                        .union(
                                from(list(
                                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                                        .select(Statistics.class, constant("max"), constant(NUM1), max(Student::age))
                        )
                        .union(
                                from(list(
                                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                                        student("smith", LocalDate.parse("1946-08-06"), "495"),
                                        student("petrov", LocalDate.parse("1996-08-06"), "494")))
                                        .select(Statistics.class, constant("average"),
                                                constant(NUM1), avg(Student::age))
                        )
                        .execute();

        Iterator<Statistics> minAndMaxAndAvgAgeIterator = minAndMaxAndAvgAge.iterator();
        assertTrue(minAndMaxAndAvgAgeIterator.hasNext());
        assertEquals(new Statistics("average", NUM1, NUM36), minAndMaxAndAvgAgeIterator.next());
        assertTrue(minAndMaxAndAvgAgeIterator.hasNext());
        assertEquals(new Statistics("max", NUM1, NUM69), minAndMaxAndAvgAgeIterator.next());
        assertTrue(minAndMaxAndAvgAgeIterator.hasNext());
        assertEquals(new Statistics("min", NUM1, NUM19), minAndMaxAndAvgAgeIterator.next());
        assertFalse(minAndMaxAndAvgAgeIterator.hasNext());
    }

    @Test
    public void testSelectWithAggregateFunctionsWithManyGroupingConditionsQuery() throws IncorrectQueryException {
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
        assertEquals(new Statistics("494", NUM1, NUM19), minAgeIterator.next());
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("494", NUM1, NUM29), minAgeIterator.next());
        assertTrue(minAgeIterator.hasNext());
        assertEquals(new Statistics("495", NUM2, NUM29), minAgeIterator.next());
        assertFalse(minAgeIterator.hasNext());
    }
}


