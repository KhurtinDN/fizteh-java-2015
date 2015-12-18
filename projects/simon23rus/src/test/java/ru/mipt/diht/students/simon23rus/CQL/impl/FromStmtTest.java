package ru.mipt.diht.students.simon23rus.CQL.impl;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery.Student.student;
import static ru.mipt.diht.students.simon23rus.CQL.data.Sources.list;
import static ru.mipt.diht.students.simon23rus.CQL.impl.FromStmt.from;

@RunWith(MockitoJUnitRunner.class)
public class FromStmtTest extends TestCase {

    List<CollectionQuery.Student> toTest;
    List<CollectionQuery.Student> emptyTest;
    Function<CollectionQuery.Student, Double> functionAge;
    Function<CollectionQuery.Student, String> functionName, functionGroup;
    CollectionQuery.Student student;

    @Before
    public void setUp() throws Exception {
        toTest = new ArrayList<>();
        emptyTest = new ArrayList<>();
        toTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        toTest.add(student("kagawa", LocalDate.parse("1966-04-03"), "456"));
        toTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        toTest.add(student("boss", LocalDate.parse("1236-08-07"), "101"));
        functionAge = CollectionQuery.Student::age;
        functionName = CollectionQuery.Student::getName;
        functionGroup = CollectionQuery.Student::getGroup;
        student = new CollectionQuery.Student("zhiraf", LocalDate.parse("1800-02-15"), "222");
    }

    @Test
    public void testFrom() throws Exception {
        FromStmt<CollectionQuery.Student> fromStmt = from(toTest);
        assertEquals(fromStmt.getData().size(), toTest.size());
        for (int i = 0; i < toTest.size(); i++) {
            assertEquals(toTest.get(i),fromStmt.getData().get(i));
        }
    }

    @Test
    public void testSelect() throws Exception {
        SelectStmt<CollectionQuery.Student, CollectionQuery.Student> select =
                from(toTest)
                .select(CollectionQuery.Student.class, CollectionQuery.Student::age);
        assertEquals(-1, select.getMaxRawsNeeded());
        assertEquals(CollectionQuery.Student.class, select.getToReturn());
        assertEquals(false, select.isDistinct);
        assertEquals(false, select.isUnion);
        Function<CollectionQuery.Student, Double> function;
        function = CollectionQuery.Student::age;
        assertEquals(1, select.getCurrentFunctions().length);
            for (CollectionQuery.Student element : toTest) {
                assertEquals(function.apply(element),
                        select.getCurrentFunctions()[0].apply(element));
            }
        assertEquals(4, select.getCurrentData().size());
        for (int i = 0; i < toTest.size(); i++) {
            assertEquals(select.getCurrentData().get(i), toTest.get(i));
        }
    }

    @Test
    public void joinTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Iterable<Tuple<String, String>> mentorsByStudent =
                from(list(student("kagawa", LocalDate.parse("1985-08-06"), "123"),
                        student("sobaka", LocalDate.parse("2000-08-06"), "123")))
                        .join(list(new CollectionQuery.Group("123", "mr.proper"),
                                new CollectionQuery.Group("123", "mr.test")))
                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                        .execute();

//        System.out.println(mentorsByStudent);
        assertEquals("[Tuple{first=kagawa, second=mr.proper}, Tuple{first=kagawa, second=mr.test}, Tuple{first=sobaka, second=mr.proper}, Tuple{first=sobaka, second=mr.test}]",
                mentorsByStudent.toString());
    }

    @Test
    public void testSelectDistinct() throws Exception {
        SelectStmt<CollectionQuery.Student, CollectionQuery.Student> select = from(toTest)
                .selectDistinct(CollectionQuery.Student.class, CollectionQuery.Student::getName,
                        CollectionQuery.Student::getGroup);
        assertEquals(-1,select.getMaxRawsNeeded());
        assertEquals(CollectionQuery.Student.class, select.getToReturn());
        assertEquals(true,select.isDistinct);
        assertEquals(false,select.isUnion);
        Function<CollectionQuery.Student, String>[] functions = new Function[2];
        functions[0] = CollectionQuery.Student::getName;
        functions[1] = CollectionQuery.Student::getGroup;
        assertEquals(select.getCurrentFunctions().length, functions.length);
        for (int i = 0; i < functions.length; i++) {
            for (CollectionQuery.Student element : toTest) {
                assertEquals(functions[i].apply(element), select.getCurrentFunctions()[i].apply(element));
            }
        }
        assertEquals(toTest.size(), select.getCurrentData().size());
        for (int i = 0; i < toTest.size(); i++) {
            assertEquals(toTest.get(i), select.getCurrentData().get(i));
        }
    }
}