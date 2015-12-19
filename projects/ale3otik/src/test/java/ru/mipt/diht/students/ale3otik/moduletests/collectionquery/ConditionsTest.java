package ru.mipt.diht.students.ale3otik.moduletests.collectionquery;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.collectionquery.Conditions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.mipt.diht.students.ale3otik.moduletests.collectionquery.CollectionQueryTest.Student;
import static ru.mipt.diht.students.ale3otik.moduletests.collectionquery.CollectionQueryTest.Student.*;

/**
 * Created by alex on 18.12.15.
 */
public class ConditionsTest extends TestCase {
    private Function<Student, String> function = Student::getName;
    List<Student> correct = new ArrayList<>();
    @Before
    public void setUp() {
        correct.add(student("malkin", LocalDate.parse("2001-03-11"), "494"));
        correct.add(student("petrov", LocalDate.parse("2000-02-12"), "495"));
        correct.add(student("semenov", LocalDate.parse("2002-01-13"), "495"));

    }

    @Test
    public void testRlike() throws Exception {
        assertEquals(Conditions.rlike(function, ".*in").test(correct.get(0)), true);
        assertEquals(Conditions.rlike(function, ".*ov").test(correct.get(1)), true);
        assertEquals(Conditions.rlike(function, ".*ov").test(correct.get(2)), true);

    }

    @Test
    public void testLike() throws Exception {
        assertEquals(Conditions.like(function, "malkin").test(correct.get(0)), true);
        assertEquals(Conditions.like(function, "malkin").test(correct.get(1)), false);
    }
}
