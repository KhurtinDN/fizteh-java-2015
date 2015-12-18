package ru.mipt.diht.students.elinrin.collectionquery.impl;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.elinrin.collectionquery.Aggregates;
import ru.mipt.diht.students.elinrin.collectionquery.CollectionQuery;
import ru.mipt.diht.students.elinrin.collectionquery.OrderByConditions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class SelectStmtTest extends TestCase {

    List<CollectionQuery.Student> exampleList, emptyExampleList, distinctExampleList;
    Function<CollectionQuery.Student, Double> functionAge;
    Function<CollectionQuery.Student, String> functionName, functionGroup;
    CollectionQuery.Student student;
    SelectStmt<CollectionQuery.Student, CollectionQuery.Student> select, distinctSelect;
    SelectStmt<CollectionQuery.Student, CollectionQuery.Statistics> groupSelect;

    @Before
    public void setUp() throws Exception {
        exampleList = new ArrayList<>();
        emptyExampleList = new ArrayList<>();
        distinctExampleList = new ArrayList<>();
        exampleList.add(new CollectionQuery.Student("garkaviy", LocalDate.parse("1997-02-20"), "495"));
        exampleList.add(new CollectionQuery.Student("ivanov", LocalDate.parse("1996-08-06"), "494"));
        exampleList.add(new CollectionQuery.Student("petrov", LocalDate.parse("1997-02-20"), "495"));
        exampleList.add(new CollectionQuery.Student("sidorov", LocalDate.parse("1996-10-29"), "495"));
        distinctExampleList.add(new CollectionQuery.Student("garkaviy", LocalDate.parse("1997-02-20"), "495"));
        distinctExampleList.add(new CollectionQuery.Student("ivanov", LocalDate.parse("1996-08-06"), "494"));
        distinctExampleList.add(new CollectionQuery.Student("garkaviy", LocalDate.parse("1997-02-20"), "495"));
        distinctExampleList.add(new CollectionQuery.Student("ivanov", LocalDate.parse("1996-08-06"), "494"));
        functionAge = CollectionQuery.Student::age;
        functionName = CollectionQuery.Student::getName;
        functionGroup = CollectionQuery.Student::getGroup;
        student = new CollectionQuery.Student("sidorov", LocalDate.parse("1996-10-29"), "495");
        select = FromStmt.from(exampleList).select(CollectionQuery.Student.class, CollectionQuery.Student::getName,
                CollectionQuery.Student::getGroup);
        distinctSelect = FromStmt.from(distinctExampleList).selectDistinct(CollectionQuery.Student.class,
                CollectionQuery.Student::getName, CollectionQuery.Student::getGroup);
        groupSelect = FromStmt.from(exampleList).select(CollectionQuery.Statistics.class,
                CollectionQuery.Student::getGroup, Aggregates.count(CollectionQuery.Student::getName));
    }

    @Test
    public void testWhere() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select
                .where(s -> Objects.equals(s.getGroup(), "494"))
                .execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
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
        resultList.add(new CollectionQuery.Statistics("495", 3));
        resultList.add(new CollectionQuery.Statistics("494", 1));
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
        resultList.add(new CollectionQuery.Student("garkaviy", "495"));
        resultList.add(new CollectionQuery.Student("petrov", "495"));
        resultList.add(new CollectionQuery.Student("sidorov", "495"));
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testHaving() throws Exception {
        List<CollectionQuery.Statistics> result = (List<CollectionQuery.Statistics>) groupSelect
                .groupBy(CollectionQuery.Student::getGroup)
                .having(s -> Objects.equals(s.getGroup(), "494"))
                .execute();
        List<CollectionQuery.Statistics> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Statistics("494", 1));
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
        resultList.add(new CollectionQuery.Student("garkaviy", "495"));
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }

        result = (List<CollectionQuery.Student>) select
                .limit(5)
                .execute();
        resultList.clear();
        resultList.add(new CollectionQuery.Student("garkaviy", "495"));
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
        resultList.add(new CollectionQuery.Student("petrov", "495"));
        resultList.add(new CollectionQuery.Student("sidorov", "495"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testExecute() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select.execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("garkaviy", "495"));
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
        resultList.add(new CollectionQuery.Student("petrov", "495"));
        resultList.add(new CollectionQuery.Student("sidorov", "495"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }

        List<CollectionQuery.Statistics> resultWithAggr = (List<CollectionQuery.Statistics>) groupSelect.execute();
        List<CollectionQuery.Statistics> resultListWithAggr = new ArrayList<>();
        resultListWithAggr.add(new CollectionQuery.Statistics("495", 1));
        resultListWithAggr.add(new CollectionQuery.Statistics("494", 1));
        resultListWithAggr.add(new CollectionQuery.Statistics("495", 1));
        resultListWithAggr.add(new CollectionQuery.Statistics("495", 1));
        assertEquals(resultWithAggr.size(), resultListWithAggr.size());
        for (int i = 0; i < resultWithAggr.size(); i++) {
            assertEquals(resultListWithAggr.get(i).toString(), resultWithAggr.get(i).toString());
        }
    }

    @Test
    public void testUnion() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) select.union().
                from(exampleList).
                select(CollectionQuery.Student.class, CollectionQuery.Student::getName,
                        CollectionQuery.Student::getGroup).
                execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("garkaviy", "495"));
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
        resultList.add(new CollectionQuery.Student("petrov", "495"));
        resultList.add(new CollectionQuery.Student("sidorov", "495"));
        resultList.add(new CollectionQuery.Student("garkaviy", "495"));
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
        resultList.add(new CollectionQuery.Student("petrov", "495"));
        resultList.add(new CollectionQuery.Student("sidorov", "495"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }

    @Test
    public void testIsDistinct() throws Exception {
        List<CollectionQuery.Student> result = (List<CollectionQuery.Student>) distinctSelect.execute();
        List<CollectionQuery.Student> resultList = new ArrayList<>();
        resultList.add(new CollectionQuery.Student("garkaviy", "495"));
        resultList.add(new CollectionQuery.Student("ivanov", "494"));
        assertEquals(resultList.size(), result.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(resultList.get(i).toString(), result.get(i).toString());
        }
    }
}
