package ru.mipt.diht.students.simon23rus.CQL.data;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery.Student.student;


@RunWith(MockitoJUnitRunner.class)
public class SourcesTest extends TestCase {

    @Test
    public void testList() throws Exception {
        List<CollectionQuery.Student> toTest = new ArrayList<>();
        toTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        toTest.add(student("kagawa", LocalDate.parse("1976-04-03"), "456"));
        toTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        toTest.add(student("boss", LocalDate.parse("1236-08-07"), "101"));

        List<CollectionQuery.Student> toReturn = Sources.list(
                student("pushkin", LocalDate.parse("1966-02-01"), "123"),
                student("kagawa", LocalDate.parse("1976-04-03"), "456"),
                student("sobaka", LocalDate.parse("1916-05-05"), "789"),
                student("boss", LocalDate.parse("1236-08-07"), "101"));
        assertEquals(toReturn.size(), toTest.size());
        for (int i = 0; i < toTest.size(); i++) {
            assertEquals(toReturn.get(i).toString(), toTest.get(i).toString());
        }
    }
}