package client;

public class StudentInfo {
        private String name;
        private String group;
        private long age;

        public StudentInfo(String name1, String group1, Long age1) {
            this.name = name1;
            this.group = group1;
            this.age = age1;
        }

        public final String getName() {
            return name;
        }

        public final String getGroup() {
            return group;
        }

        public final long getAge() {
            return age;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StudentInfo that = (StudentInfo) o;

            if (age != that.age) return false;
            if (!name.equals(that.name)) return false;
            return group.equals(that.group);

        }

        @Override
        public final int hashCode() {
            final int const31 = 31;
            final int const32 = 32;

            int result = name.hashCode();
            result = const31 * result + group.hashCode();
            result = const31 * result + (int) (age ^ (age >>> const32));
            return result;
        }

        @Override
        public final String toString() {
            return "StudentInfo{"
                    + "name=" + name
                    + ", group=" + group
                    + ", age=" + age
                    + '}';
        }
    }