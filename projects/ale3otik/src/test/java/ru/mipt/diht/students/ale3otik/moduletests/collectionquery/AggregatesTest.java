package ru.mipt.diht.students.ale3otik.moduletests.collectionquery;

/**
 * Created by alex on 18.12.15.
 */
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.ale3otik.collectionquery.Aggregates;
import ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl.Aggregator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.mipt.diht.students.ale3otik.moduletests.collectionquery.CollectionQueryTest.Student;

@RunWith(MockitoJUnitRunner.class)
public class AggregatesTest extends TestCase {

    private List<Student> correct, emptylist;
    private Function<Student, Long> functionAge;
    private Function<Student, String> functionName, functionGroup;
    private Student student;

    @Before
    public void setUp() throws Exception {
        correct = new ArrayList<>();
        emptylist = new ArrayList<>();
        correct.add(new Student("petrov", LocalDate.parse("1997-03-30"), "494"));
        correct.add(new Student("ivanov", LocalDate.parse("1997-01-01"), "495"));
        correct.add(new Student("stepanov", LocalDate.parse("1997-02-02"), null));
        functionAge = Student::age;
        functionName = Student::getName;
        functionGroup = Student::getGroup;
        student = new Student("superman", LocalDate.parse("1996-10-29"), "495");
    }

    @Test
    public void testMax() throws Exception {
//        System.out.println(((Aggregator)Aggregates.max(functionGroup)).apply(correct));

        assertEquals(((Aggregator) Aggregates.max(functionGroup)).apply(correct), "495");
        assertEquals(((Aggregator) Aggregates.max(functionName)).apply(correct), "stepanov");
        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(correct), 3L);

//        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(emptylist), null);
    }

    @Test
    public void testMin() throws Exception {
        assertEquals(((Aggregator) Aggregates.min(functionGroup)).apply(correct), "494");
        assertEquals(((Aggregator) Aggregates.min(functionName)).apply(correct), "ivanov");
        assertEquals(((Aggregator) Aggregates.min(functionAge)).apply(correct), 3L);
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(((Aggregator) Aggregates.count(functionGroup)).apply(correct), 2L);
        assertEquals(((Aggregator) Aggregates.count(functionName)).apply(correct), 3L);
        assertEquals(((Aggregator) Aggregates.count(functionAge)).apply(correct), 3L);
    }

    @Test
    public void testAvg() throws Exception {
        assertEquals((Double) ((Aggregator) Aggregates.avg(functionAge)).apply(correct), 3.0, 0.1);
    }
}
