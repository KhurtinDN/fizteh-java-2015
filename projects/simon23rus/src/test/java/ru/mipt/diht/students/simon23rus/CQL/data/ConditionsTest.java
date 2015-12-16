package ru.mipt.diht.students.simon23rus.CQL.data;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery.Student.student;
import static ru.mipt.diht.students.simon23rus.CQL.data.Conditions.rlike;

@RunWith(MockitoJUnitRunner.class)
public class ConditionsTest extends TestCase {
    Function<CollectionQuery.Student, String> function = CollectionQuery.Student::getName;

    @Test
    public void testRlike() throws Exception {
        List<CollectionQuery.Student> toTest = new ArrayList<>();
        toTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        toTest.add(student("kagawa", LocalDate.parse("1976-04-03"), "456"));
        toTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        toTest.add(student("boss", LocalDate.parse("1236-08-07"), "101"));

        assertEquals(true, rlike(function, ".*in").test(toTest.get(0)));
        assertEquals(true, rlike(function, ".*awa").test(toTest.get(1)));
        assertEquals(false, rlike(function, ".*ov").test(toTest.get(2)));
        assertEquals(false, rlike(function, ".*ov").test(toTest.get(3)));
    }

}