package ru.mipt.diht.students.ale3otik.moduleTests;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.miniorm.DatabaseService;

/**
 * Created by alex on 15.12.15.
 */
public class ormTest extends TestCase{

    @DatabaseService.Table(name = "users")
    public static class User {
        public User(){};
        @DatabaseService.Column(name = "name")
        @DatabaseService.PrimaryKey
        public String name;

        @DatabaseService.Column(name = "age")
        public int age;

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

    @Test
    public void testOrm() throws Exception {
//        User user = new User();
//        user.setName("Alex");
//        user.setAge(18);


        DatabaseService<User> db = new DatabaseService<User>(User.class);
        db.createTable();
//        db.insert(user);
        db.queryForAll();
    }
}
