package ru.mipt.diht.students.maxdankow.sqlcollections;

public class Statistics {

    private final String group;
    private final Long count;
    private final Long age;

    public String getGroup() {
        return group;
    }

    public Long getCount() {
        return count;
    }

    public Long getAge() {
        return age;
    }

    public Statistics(String group, Long count, Long age) {
        this.group = group;
        this.count = count;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Statistics{"
                + "group='" + group + '\''
                + ", count=" + count
                + ", age=" + age
                + '}';
    }
}
