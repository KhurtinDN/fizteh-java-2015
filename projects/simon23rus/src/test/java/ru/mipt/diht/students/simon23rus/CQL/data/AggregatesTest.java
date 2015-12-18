package ru.mipt.diht.students.simon23rus.CQL.data;

import ru.mipt.diht.students.simon23rus.CQL.implOfAggregators.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class AggregatesTest extends TestCase {

    List<CollectionQuery.Student> toTest;
    List<CollectionQuery.Student> emptyTest;
    Function<CollectionQuery.Student, Double> functionAge;
    Function<CollectionQuery.Student, String> functionName, functionGroup;
    CollectionQuery.Student student;

    @Before
    public void setUp() throws Exception {
        toTest = new ArrayList<>();
        emptyTest = new ArrayList<>();
        toTest.add(new CollectionQuery.Student("sobaka", LocalDate.parse("2007-08-06"), "123"));
        toTest.add(new CollectionQuery.Student("sinitsa", LocalDate.parse("2007-08-18"), "456"));
        toTest.add(new CollectionQuery.Student("vorobey", LocalDate.parse("1996-03-14"), "789"));
        toTest.add(new CollectionQuery.Student("drakon", LocalDate.parse("1000-02-13"), "223"));
        functionAge = CollectionQuery.Student::age;
        functionName = CollectionQuery.Student::getName;
        functionGroup = CollectionQuery.Student::getGroup;
        student = new CollectionQuery.Student("zhiraf", LocalDate.parse("1996-10-29"), "788");
    }

    @Test
    public void testMax() throws Exception {
        assertEquals("789", ((Aggregator) Aggregates.max(functionGroup)).apply(toTest));
        assertEquals("vorobey", ((Aggregator) Aggregates.max(functionName)).apply(toTest));
        assertEquals(1015D, ((Aggregator) Aggregates.max(functionAge)).apply(toTest));

        assertEquals(19D, ((Aggregator) Aggregates.max(functionAge)).apply(student));
        assertEquals(null, ((Aggregator) Aggregates.max(functionAge)).apply(emptyTest));
    }

    @Test
    public void testMin() throws Exception {
        assertEquals("123", ((Aggregator) Aggregates.min(functionGroup)).apply(toTest));
        assertEquals("drakon", ((Aggregator) Aggregates.min(functionName)).apply(toTest));
        assertEquals(8D, ((Aggregator) Aggregates.min(functionAge)).apply(toTest));

        assertEquals(19D, ((Aggregator) Aggregates.min(functionAge)).apply(student));
        assertEquals(null, ((Aggregator) Aggregates.min(functionAge)).apply(emptyTest));
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(4L,((Aggregator) Aggregates.count(functionGroup)).apply(toTest));
        assertEquals(4L,((Aggregator) Aggregates.count(functionName)).apply(toTest));
        assertEquals(3L,((Aggregator) Aggregates.count(functionAge)).apply(toTest));

        assertEquals(1L,((Aggregator) Aggregates.count(functionAge)).apply(student));
    }

    @Test
    public void testAvg() throws Exception {
        assertEquals(262.5,(Double)((Aggregator) Aggregates.avg(functionAge)).apply(toTest) ,0.1);

        assertEquals(19D, ((Aggregator) Aggregates.avg(functionAge)).apply(student));
    }
}