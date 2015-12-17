package ru.mipt.diht.students.maxDankow.sqlcollections;

public class CollectionQueryTests {
//       Iterable<Statistics> statistics =
//                from(list(
//                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
//                        student("ivanov", LocalDate.parse("1986-08-06"), "494")))
//                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
//                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
//                        .groupBy(Student::getGroup)
//                        .having(s -> s.getCount() > 0)
//                        .orderBy(asc(Student::getGroup), desc(count(Student::getGroup)))
//                        .limit(100)
//                        .union()
//                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
//                        .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
//                        .execute();
//        System.out.println(statistics);
}
