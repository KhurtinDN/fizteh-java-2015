package ru.mipt.diht.students.elinrin.collectionquery.impl;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.elinrin.collectionquery.CollectionQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class FromStmtTest extends TestCase {

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
    public void testFrom() throws Exception {
        FromStmt<CollectionQuery.Student> fromStmt = FromStmt.from(exampleList);
        assertEquals(fromStmt.getElements().size(), exampleList.size());
        for (int i = 0; i < exampleList.size(); i++) {
            assertEquals(fromStmt.getElements().get(i), exampleList.get(i));
        }
    }

    @Test
    public void testSelect() throws Exception {
        SelectStmt<CollectionQuery.Student, CollectionQuery.Student> select = FromStmt.from(exampleList)
                .select(CollectionQuery.Student.class, CollectionQuery.Student::getName,
                        CollectionQuery.Student::getGroup);
        assertEquals(select.getNumberOfObjects(), -1);
        assertEquals(select.getReturnClass(), CollectionQuery.Student.class);
        assertEquals(select.isDistinct(), false);
        assertEquals(select.isUnion(), false);
        Function<CollectionQuery.Student, String>[] functions = new Function[2];
        functions[0] = CollectionQuery.Student::getName;
        functions[1] = CollectionQuery.Student::getGroup;
        assertEquals(select.getFunctions().length, functions.length);
        for (int i = 0; i < functions.length; i++) {
            for (CollectionQuery.Student element : exampleList) {
                assertEquals(functions[i].apply(element),
                        select.getFunctions()[i].apply(element));
            }
        }
        assertEquals(exampleList.size(), select.getElements().size());
        for (int i = 0; i < exampleList.size(); i++) {
            assertEquals(exampleList.get(i), select.getElements().get(i));
        }
    }

    @Test
    public void testSelectDistinct() throws Exception {
        SelectStmt<CollectionQuery.Student, CollectionQuery.Student> select = FromStmt.from(exampleList)
                .selectDistinct(CollectionQuery.Student.class, CollectionQuery.Student::getName,
                        CollectionQuery.Student::getGroup);
        assertEquals(select.getNumberOfObjects(), -1);
        assertEquals(select.getReturnClass(), CollectionQuery.Student.class);
        assertEquals(select.isDistinct(), true);
        assertEquals(select.isUnion(), false);
        Function<CollectionQuery.Student, String>[] functions = new Function[2];
        functions[0] = CollectionQuery.Student::getName;
        functions[1] = CollectionQuery.Student::getGroup;
        assertEquals(select.getFunctions().length, functions.length);
        for (int i = 0; i < functions.length; i++) {
            for (CollectionQuery.Student element : exampleList) {
                assertEquals(functions[i].apply(element),
                        select.getFunctions()[i].apply(element));
            }
        }
        assertEquals(exampleList.size(), select.getElements().size());
        for (int i = 0; i < exampleList.size(); i++) {
            assertEquals(exampleList.get(i), select.getElements().get(i));
        }
    }
}
