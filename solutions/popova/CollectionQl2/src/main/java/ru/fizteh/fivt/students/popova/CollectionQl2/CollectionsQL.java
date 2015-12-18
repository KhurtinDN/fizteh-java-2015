package ru.fizteh.fivt.students.popova.CollectionQl2;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by V on 19.12.2015.
 */
public class CollectionsQL {

    /**
     *
     * @param <T>
     * @param collection
     * @return
     */
    /*public static<T> Selector from(AbstractCollection<T> collection)
    {
        return new Selector<>(collection);
    }*/

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList students = new ArrayList<Student>();
        students.add(new Student("ivanov", LocalDate.parse("1986-08-06"), "494"));
        students.add(new Student("sidorov", LocalDate.parse("1999-08-06"), "495"));
        students.add(new Student("john", LocalDate.parse("1987-08-06"), "494"));
        Query<Student,Statistics> q = new Query<Student, Statistics>(students);
        Iterable<Statistics> it = q.where(Conditions.rlike(Student::getName, ".*ov")).orderBy(Comparators.desc(Student::getGroup)).groupBy(Student::getGroup).select(Statistics.class, Student::getGroup).having(s -> s.getCount() > 0).execute();
        System.out.println(it);
    }
}
