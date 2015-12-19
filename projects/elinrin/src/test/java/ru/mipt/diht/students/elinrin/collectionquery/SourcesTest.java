package ru.mipt.diht.students.elinrin.collectionquery;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.mipt.diht.students.elinrin.collectionquery.CollectionQuery.Student.student;

@RunWith(MockitoJUnitRunner.class)
public class SourcesTest extends TestCase {

    @Test
    public void testList() throws Exception {
        List<CollectionQuery.Student> exampleList = new ArrayList<>();
        exampleList.add(student("ivanov", LocalDate.parse("1986-08-06"), "496"));
        exampleList.add(student("petrov", LocalDate.parse("1986-08-06"), "495"));
        exampleList.add(student("sidorov", LocalDate.parse("1986-08-06"), "495"));

        List<CollectionQuery.Student> resultList = Sources.list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "496"),
                        student("petrov", LocalDate.parse("1986-08-06"), "495"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"));
        assertEquals(exampleList.size(), resultList.size());
        for (int i = 0; i < exampleList.size(); i++) {
            assertEquals(exampleList.get(i).toString(), resultList.get(i).toString());
        }
    }
}
