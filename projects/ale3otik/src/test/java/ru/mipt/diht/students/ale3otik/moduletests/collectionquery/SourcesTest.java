package ru.mipt.diht.students.ale3otik.moduletests.collectionquery;

import static ru.mipt.diht.students.ale3otik.moduletests.collectionquery.CollectionQueryTest.Student;
import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.collectionquery.Sources;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 18.12.15.
 */
public class SourcesTest extends TestCase {
    @Test
    public void testList() throws Exception {
        List<Student> correct = new ArrayList<>();
        correct.add(new Student("zharkov", LocalDate.parse("2012-12-17"), "494"));
        correct.add(new Student("gezhes", LocalDate.parse("2011-12-17"), "495"));
        correct.add(new Student("popov", LocalDate.parse("2010-12-17"), "495"));

        List<Student> result = Sources.list(
                new Student("zharkov", LocalDate.parse("2012-12-17"), "494"),
                new Student("gezhes", LocalDate.parse("2011-12-17"), "495"),
                new Student("popov", LocalDate.parse("2010-12-17"), "495"));
        assertEquals(correct,result);
    }
}
