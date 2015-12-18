package ru.fizteh.fivt.students.vruchtel.collectionsql;

/**
 * Created by Серафима on 18.12.2015.
 */
public class Group {
    private final String group;
    private final String mentor;

    public Group(String _group, String _mentor) {
        group = _group;
        mentor = _mentor;
    }

    public String getGroup() {
        return group;
    }

    public String getMentor() {
        return mentor;
    }

    public static Group group(String _group, String _mentor) {
        return new Group(_group, _mentor);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append("Student{");
        if (group != null) {
            result.append("group='").append(group).append('\'');
        }
        if (mentor != null) {
            result.append(", name=").append(mentor);
        }
        result.append("}\n");
        return result.toString();
    }
}