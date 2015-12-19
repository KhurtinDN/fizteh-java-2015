package ru.mipt.diht.students.simon23rus.CQL.impl;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.simon23rus.CQL.data.Aggregates;
import ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery;
import ru.mipt.diht.students.simon23rus.CQL.data.OrderByConditions;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery.Student.student;

@RunWith(MockitoJUnitRunner.class)
public class SelectStmtTest extends TestCase {

    List<CollectionQuery.Student> toTest, emptyExampleList, distinctTest;
    Function<CollectionQuery.Student, Double> functionAge;
    Function<CollectionQuery.Student, String> functionName, functionGroup;
    CollectionQuery.Student student;
    SelectStmt<CollectionQuery.Student, CollectionQuery.Student> select, distinctSelect;
    SelectStmt<CollectionQuery.Student, CollectionQuery.Statistics> groupSelect;

    @Before
    public void setUp() throws Exception {
        toTest = new ArrayList<>();
        emptyExampleList = new ArrayList<>();
        distinctTest = new ArrayList<>();
        toTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        toTest.add(student("kagawa", LocalDate.parse("1966-04-03"), "456"));
        toTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        toTest.add(student("boss", LocalDate.parse("1236-08-07"), "123"));
        distinctTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        distinctTest.add(student("pushkin", LocalDate.parse("1966-02-01"), "123"));
        distinctTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        distinctTest.add(student("sobaka", LocalDate.parse("1916-05-05"), "789"));
        functionAge = CollectionQuery.Student::age;
        functionName = CollectionQuery.Student::getName;
        functionGroup = CollectionQuery.Student::getGroup;
        student = new CollectionQuery.Student("zhiraf", LocalDate.parse("1996-02-12"), "123");
        select = FromStmt.from(toTest).select(CollectionQuery.Student.class, CollectionQuery.Student::getName,
                CollectionQuery.Student::getGroup);
        distinctSelect = FromStmt.from(distinctTest).selectDistinct(CollectionQuery.Student.class,
                CollectionQuery.Student::getName, CollectionQuery.Student::getGroup);
        groupSelect = FromStmt.from(toTest).select(CollectionQuery.Statistics.class,
                CollectionQuery.Student::getGroup, Aggregates.count(CollectionQuery.Student::getName));
    }

    @Test
    public void testWhere() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select
                .where(s -> Objects.equals(s.getGroup(), "456"))
                .execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("kagawa", "456"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testGroupBy() throws Exception {
        List<CollectionQuery.Statistics> result = (List<CollectionQuery.Statistics>) groupSelect
                .groupBy(CollectionQuery.Student::getGroup)
                .execute();
        List<CollectionQuery.Statistics> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Statistics("123", 2L));
        resultList.add(new CollectionQuery.Statistics("456", 1L));
        resultList.add(new CollectionQuery.Statistics("789", 1L));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testOrderBy() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select
                .orderBy(OrderByConditions.desc(CollectionQuery.Student::getGroup))
                .execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("sobaka", "789"));
        resultList.add(new CollectionQuery.Student("kagawa", "456"));
        resultList.add(new CollectionQuery.Student("pushkin", "123"));
        resultList.add(new CollectionQuery.Student("boss", "123"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testHaving() throws Exception {
        List<CollectionQuery.Statistics> result = (List<CollectionQuery.Statistics>) groupSelect
                .groupBy(CollectionQuery.Student::getGroup)
                .having(s -> Objects.equals(s.getGroup(), "123"))
                .execute();
        System.out.println(result);
        List<CollectionQuery.Statistics> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Statistics("123", 2L));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testLimit() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select
                .limit(2)
                .execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("pushkin", "123"));
        resultList.add(new CollectionQuery.Student("kagawa", "456"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }

        result = (List<CollectionQuery.Student>) select
                .limit(5)
                .execute();
        resultList.clear();
        resultList.add(new CollectionQuery.Student("pushkin", "123"));
        resultList.add(new CollectionQuery.Student("kagawa", "456"));
        resultList.add(new CollectionQuery.Student("sobaka", "789"));
        resultList.add(new CollectionQuery.Student("boss", "123"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testExecute() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select.execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("pushkin", "123"));
        resultList.add(new CollectionQuery.Student("kagawa", "456"));
        resultList.add(new CollectionQuery.Student("sobaka", "789"));
        resultList.add(new CollectionQuery.Student("boss", "123"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }

        List<CollectionQuery.Statistics> resultWithAggr = (List<CollectionQuery.Statistics>) groupSelect.execute();
        List<CollectionQuery.Statistics> resultListWithAggr = new ArrayList<>();
        resultListWithAggr.add(new CollectionQuery.Statistics("123", 1L));
        resultListWithAggr.add(new CollectionQuery.Statistics("456", 1L));
        resultListWithAggr.add(new CollectionQuery.Statistics("789", 1L));
        resultListWithAggr.add(new CollectionQuery.Statistics("123", 1L));
        assertEquals(resultListWithAggr.size(), resultWithAggr.size());
        for (int i = 0; i < resultWithAggr.size(); i++) {
            assertEquals(resultListWithAggr.get(i).toString(), resultWithAggr.get(i).toString());
        }
    }

    @Test
    public void testUnion() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select.union().
                from(toTest).
                select(CollectionQuery.Student.class, CollectionQuery.Student::getName,
                        CollectionQuery.Student::getGroup).
                execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("pushkin", "123"));
        resultList.add(new CollectionQuery.Student("kagawa", "456"));
        resultList.add(new CollectionQuery.Student("sobaka", "789"));
        resultList.add(new CollectionQuery.Student("boss", "123"));
        resultList.add(new CollectionQuery.Student("pushkin", "123"));
        resultList.add(new CollectionQuery.Student("kagawa", "456"));
        resultList.add(new CollectionQuery.Student("sobaka", "789"));
        resultList.add(new CollectionQuery.Student("boss", "123"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testIsDistinct() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) distinctSelect.execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("pushkin", "123"));
        resultList.add(new CollectionQuery.Student("sobaka", "789"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }
}