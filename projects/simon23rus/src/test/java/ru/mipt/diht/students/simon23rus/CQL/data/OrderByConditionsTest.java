package ru.mipt.diht.students.simon23rus.CQL.data;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery.Student.student;

@RunWith(MockitoJUnitRunner.class)
public class OrderByConditionsTest extends TestCase {
    Function<CollectionQuery.Student, String> nameFunction = CollectionQuery.Student::getName;

    @Test
    public void testAsc() throws Exception {
        List<CollectionQuery.Student> toTest = new ArrayList<>();
        toTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        toTest.add(student("kagawa", LocalDate.parse("1976-04-03"), "456"));
        toTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        toTest.add(student("boss", LocalDate.parse("1236-08-07"), "101"));
        assertTrue(OrderByConditions.asc(nameFunction).compare(toTest.get(0), toTest.get(1)) > 0);
        assertTrue(OrderByConditions.asc(nameFunction).compare(toTest.get(1), toTest.get(0)) < 0);
        assertTrue(OrderByConditions.asc(nameFunction).compare(toTest.get(2), toTest.get(2)) == 0);
        assertTrue(OrderByConditions.asc(nameFunction).compare(toTest.get(2), toTest.get(3)) > 0);
    }

    @Test
    public void testDesc() throws Exception {
        List<CollectionQuery.Student> toTest = new ArrayList<>();
        toTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        toTest.add(student("kagawa", LocalDate.parse("1976-04-03"), "456"));
        toTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        toTest.add(student("boss", LocalDate.parse("1236-08-07"), "101"));
        assertTrue(OrderByConditions.desc(nameFunction).compare(toTest.get(0), toTest.get(1)) < 0);
        assertTrue(OrderByConditions.desc(nameFunction).compare(toTest.get(1), toTest.get(0)) > 0);
        assertTrue(OrderByConditions.desc(nameFunction).compare(toTest.get(2), toTest.get(2)) == 0);
        assertTrue(OrderByConditions.desc(nameFunction).compare(toTest.get(2), toTest.get(3)) < 0);
    }
}