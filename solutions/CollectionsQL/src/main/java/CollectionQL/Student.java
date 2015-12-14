package CollectionQL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by V on 30.11.2015.
 */
public class Student {
    private final String name;

    private final LocalDate dateOfBith;

    private final String group;

    public String getName() {
        return name;
    }

    public Student(String name, LocalDate dateOfBith, String group) {
        this.name = name;
        this.dateOfBith = dateOfBith;
        this.group = group;
    }

    public LocalDate getDateOfBith() {
        return dateOfBith;
    }

    public String getGroup() {
        return group;
    }

    public long age() {
        return ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
    }

    public static Student student(String name, LocalDate dateOfBith, String group) {
        return new Student(name, dateOfBith, group);
    }
}
