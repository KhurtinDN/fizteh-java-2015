package ru.mipt.diht.students.elinrin.collectionquery;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl.Aggregator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class AggregatesTest extends TestCase {

    List<CollectionQuery.Student> exampleList, emptyExampleList;
    Function<CollectionQuery.Student, Double> functionAge;
    Function<CollectionQuery.Student, String> functionName, functionGroup;
    CollectionQuery.Student student;

    @Before
    public void setUp() throws Exception {
        exampleList = new ArrayList<>();
        emptyExampleList = new ArrayList<>();
        exampleList.add(new CollectionQuery.Student("ivanov", LocalDate.parse("1996-08-06"), "494"));
        exampleList.add(new CollectionQuery.Student("petrov", LocalDate.parse("1997-02-20"), "495"));
        exampleList.add(new CollectionQuery.Student("sidorov", LocalDate.parse("1996-10-29"), "495"));
        functionAge = CollectionQuery.Student::age;
        functionName = CollectionQuery.Student::getName;
        functionGroup = CollectionQuery.Student::getGroup;
        student = new CollectionQuery.Student("sidorov", LocalDate.parse("1996-10-29"), "495");
    }

    @Test
    public void testMax() throws Exception {
        assertEquals(((Aggregator) Aggregates.max(functionGroup)).apply(exampleList), "495");
        assertEquals(((Aggregator) Aggregates.max(functionName)).apply(exampleList), "sidorov");
        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(exampleList), 19.0);

        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(student), null);
        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(emptyExampleList), null);
    }

    @Test
    public void testMin() throws Exception {
        assertEquals(((Aggregator) Aggregates.min(functionGroup)).apply(exampleList), "494");
        assertEquals(((Aggregator) Aggregates.min(functionName)).apply(exampleList), "ivanov");
        assertEquals(((Aggregator) Aggregates.min(functionAge)).apply(exampleList), 18.0);

        assertEquals(((Aggregator) Aggregates.min(functionAge)).apply(student), null);
        assertEquals(((Aggregator) Aggregates.min(functionAge)).apply(emptyExampleList), null);
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(((Aggregator) Aggregates.count(functionGroup)).apply(exampleList), 2);
        assertEquals(((Aggregator) Aggregates.count(functionName)).apply(exampleList), 3);
        assertEquals(((Aggregator) Aggregates.count(functionAge)).apply(exampleList), 2);

        assertEquals(((Aggregator) Aggregates.count(functionAge)).apply(student), null);
    }

    @Test
    public void testAvg() throws Exception {
        assertEquals((Double)((Aggregator) Aggregates.avg(functionAge)).apply(exampleList), 18.666, 0.1);

        assertEquals(((Aggregator) Aggregates.avg(functionAge)).apply(student), null);
    }
}
