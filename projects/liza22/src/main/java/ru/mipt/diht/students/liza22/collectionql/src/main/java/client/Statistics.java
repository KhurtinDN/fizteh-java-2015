package client;

public class Statistics {

    private final String group;
    private final Long count;
    private final Long age;
    private final int const1 = 31;

    public final String getGroup() {
        return group;
    }

    public final Long getCount() {
        return count;
    }

    public final Long getAge() {
        return age;
    }

    public Statistics(String group1, Long count1, Long age1) {
        this.group = group1;
        this.count = count1;
        this.age = age1;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Statistics that = (Statistics) o;

        if (!group.equals(that.group)) {
            return false;
        }
        if (!count.equals(that.count)) {
            return false;
        }
        return age.equals(that.age);

    }

    @Override
    public final int hashCode() {
        int result = group.hashCode();
        result = const1 * result + count.hashCode();
        result = const1 * result + age.hashCode();
        return result;
    }

    @Override
    public final String toString() {
        return "Statistics{"
                + "group=" + group
                + ", count=" + count
                + ", avg=" + age
                + '}';
    }
}
