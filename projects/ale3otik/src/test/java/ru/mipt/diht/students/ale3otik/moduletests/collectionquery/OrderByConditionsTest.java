package ru.mipt.diht.students.ale3otik.moduletests.collectionquery;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.collectionquery.OrderByConditions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.mipt.diht.students.ale3otik.moduletests.collectionquery.CollectionQueryTest.Student;
import static ru.mipt.diht.students.ale3otik.moduletests.collectionquery.CollectionQueryTest.Student.*;

/**
 * Created by alex on 18.12.15.
 */
public class OrderByConditionsTest {
    private Function<Student, String> function = Student::getName;

    @Test
    public void testAsc() throws Exception {
        List<Student> correct = new ArrayList<>();
        correct.add(student("ivanov", LocalDate.parse("2001-12-12"), "494"));
        correct.add(student("minkin", LocalDate.parse("2002-11-12"), "495"));
        correct.add(student("stepanov", LocalDate.parse("1999-02-12"), "495"));
        assert(OrderByConditions.asc(function).compare(correct.get(0), correct.get(2)) < 0);
        assert(OrderByConditions.asc(function).compare(correct.get(1), correct.get(0)) > 0);
        assert(OrderByConditions.asc(function).compare(correct.get(0), correct.get(0)) == 0);
    }

    @Test
    public void testDesc() throws Exception {
        List<Student> correct = new ArrayList<>();
        correct.add(student("minkin", LocalDate.parse("2001-12-12"), "494"));
        correct.add(student("ivanov", LocalDate.parse("2002-11-12"), "495"));
        correct.add(student("stepanov", LocalDate.parse("1999-02-12"), "495"));
        assert(OrderByConditions.desc(function).compare(correct.get(0), correct.get(2)) > 0);
        assert(OrderByConditions.desc(function).compare(correct.get(1), correct.get(0)) > 0);
        assert(OrderByConditions.desc(function).compare(correct.get(0), correct.get(0)) == 0);
    }
}
