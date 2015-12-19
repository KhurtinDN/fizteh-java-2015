package ru.mipt.diht.students.elinrin.collectionquery;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.mipt.diht.students.elinrin.collectionquery.CollectionQuery.Student.student;

@RunWith(MockitoJUnitRunner.class)
public class OrderByConditionsTest extends TestCase {
    Function<CollectionQuery.Student, String> function = CollectionQuery.Student::getName;

    @Test
    public void testAsc() throws Exception {
        List<CollectionQuery.Student> exampleList = new ArrayList<>();
        exampleList.add(student("ivanov", LocalDate.parse("1986-08-06"), "496"));
        exampleList.add(student("petrov", LocalDate.parse("1986-08-06"), "497"));
        exampleList.add(student("vasilev", LocalDate.parse("1986-08-06"), "496"));
        assertTrue(OrderByConditions.asc(function).compare(exampleList.get(0), exampleList.get(2)) < 0);
        assertTrue(OrderByConditions.asc(function).compare(exampleList.get(1), exampleList.get(0)) > 0);
        assertTrue(OrderByConditions.asc(function).compare(exampleList.get(0), exampleList.get(0)) == 0);
    }

    @Test
    public void testDesc() throws Exception {
        List<CollectionQuery.Student> exampleList = new ArrayList<>();
        exampleList.add(student("ivanov", LocalDate.parse("1986-08-06"), "494"));
        exampleList.add(student("petrov", LocalDate.parse("1986-08-06"), "495"));
        exampleList.add(student("sidorov", LocalDate.parse("1986-08-06"), "495"));
        assertTrue(OrderByConditions.desc(function).compare(exampleList.get(0), exampleList.get(2)) > 0);
        assertTrue(OrderByConditions.desc(function).compare(exampleList.get(1), exampleList.get(0)) < 0);
        assertTrue(OrderByConditions.desc(function).compare(exampleList.get(0), exampleList.get(0)) == 0);
    }
}
