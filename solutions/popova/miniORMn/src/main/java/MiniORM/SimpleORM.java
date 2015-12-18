package MiniORM;

/**
 * Created by SM1 on 18.12.2015.
 */
import java.util.List;

public class SimpleORM {
    @Table(name = "users2")
    static class User {
        @PrimaryKey()
        @Column(name = "id")
        Integer id;

        @Column(name = "name")
        String name;

        @Column(name = "age")
        Integer age;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {

        User user = new User();
        user.id = 4;
        user.setName("Korolev");
        user.setAge(17);

        DataServices<User> orm =
                new DataServices<User>(User.class,"jdbc:h2:~/testORM","","");

      //  orm.createTable();
       // orm.insert(user);
       // orm.delete(user);
       // orm.update(user);

        List<User> l= orm.queryForAll();

        for (User user2 : l) {
            System.out.println(user2);
        }
    }
}
