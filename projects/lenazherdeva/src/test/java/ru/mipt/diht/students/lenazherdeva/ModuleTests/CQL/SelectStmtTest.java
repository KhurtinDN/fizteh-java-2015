/*package ru.mipt.diht.students.lenazherdeva.moduleTests.CQL;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.lenazherdeva.CQL.Aggregates;
import ru.mipt.diht.students.lenazherdeva.CQL.CQL;
import ru.mipt.diht.students.lenazherdeva.CQL.OrderByConditions;
import ru.mipt.diht.students.lenazherdeva.CQL.impl.FromStmt;
import ru.mipt.diht.students.lenazherdeva.CQL.impl.SelectStmt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by admin on 16.12.2015.


@RunWith(MockitoJUnitRunner.class)
public class SelectStmtTest extends TestCase {

    List<CQL.Student> exampleList, emptyExampleList, distinctExampleList;
    Function<CQL.Student, Double> functionAge;
    Function<CQL.Student, String> functionName, functionGroup;
    CQL.Student student;
    SelectStmt<CQL.Student, CQL.Student> select, distinctSelect;
    SelectStmt<CQL.Student, CQL.Statistics> groupSelect;


    @Before
    public void setUp() throws Exception {
        exampleList = new ArrayList<>();
        emptyExampleList = new ArrayList<>();
        distinctExampleList = new ArrayList<>();
        exampleList.add(new CQL.Student("ivanov", LocalDate.parse("1997-02-20"), "497"));
        exampleList.add(new CQL.Student("ako", LocalDate.parse("1996-08-06"), "494"));
        exampleList.add(new CQL.Student("yam", LocalDate.parse("1997-02-20"), "497"));
        exampleList.add(new CQL.Student("tas", LocalDate.parse("1996-10-29"), "497"));
        distinctExampleList.add(new CQL.Student("ivanov", LocalDate.parse("1997-02-20"), "497"));
        distinctExampleList.add(new CQL.Student("ako", LocalDate.parse("1996-08-06"), "494"));
        distinctExampleList.add(new CQL.Student("ivanov", LocalDate.parse("1997-02-20"), "497"));
        distinctExampleList.add(new CQL.Student("ako", LocalDate.parse("1996-08-06"), "494"));
        functionAge = CQL.Student::age;
        functionName = CQL.Student::getName;
        functionGroup = CQL.Student::getGroup;
        student = new CQL.Student("tas", LocalDate.parse("1996-10-29"), "497");
        select = FromStmt.from(exampleList).select(CQL.Student.class, CQL.Student::getName,
                CQL.Student::getGroup);
        distinctSelect = FromStmt.from(distinctExampleList).selectDistinct(CQL.Student.class,
                CQL.Student::getName, CQL.Student::getGroup);
        groupSelect = FromStmt.from(exampleList).select(CQL.Statistics.class,
                CQL.Student::getGroup, Aggregates.count(CQL.Student::getName));
    }

}
*/
