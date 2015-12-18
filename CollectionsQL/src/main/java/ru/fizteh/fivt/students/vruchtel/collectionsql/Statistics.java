package ru.fizteh.fivt.students.vruchtel.collectionsql;

/**
 * Created by Серафима on 18.12.2015.
 */
public class Statistics {
    private final String group;
    private final Integer count;
    private final Double age;

    public Statistics(String _group, Integer _count) {
        group = _group;
        count = _count;
        age = null;
    }

    public Statistics(String _group, Integer _count, Double _age) {
        group = _group;
        count = _count;
        age = _age;
    }

    public Statistics(String _group) {
        group = _group;
        count = null;
        age = null;
    }

    public String getGroup() {
        return group;
    }

    public Integer getCount() {
        return count;
    }

    public Double getAge() {
        return age;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append("Statistics{");
        if (group != null) {
            result.append("group='").append(group).append('\'');
        }
        if (count != null) {
            result.append(", count=").append(count);
        }
        if (age != null) {
            result.append(", age=").append(age);
        }
        result.append("}\n");
        return result.toString();
    }
}

