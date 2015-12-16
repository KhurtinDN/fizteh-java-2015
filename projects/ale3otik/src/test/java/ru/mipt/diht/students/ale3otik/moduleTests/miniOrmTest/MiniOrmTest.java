package ru.mipt.diht.students.ale3otik.moduleTests;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.miniorm.DatabaseService;
import ru.mipt.diht.students.ale3otik.miniorm.DatabaseService.Column;
import ru.mipt.diht.students.ale3otik.miniorm.DatabaseService.Table;
import ru.mipt.diht.students.ale3otik.miniorm.DatabaseService.PrimaryKey;

import java.util.Date;
import java.util.List;

/**
 * Created by alex on 15.12.15.
 */
public class MiniOrmTest extends TestCase {

    @DatabaseService.Table
    public static class SimpleStudent {
        public SimpleStudent() {
        }

        ;

        public SimpleStudent(String newName, int newAge, Date newBirthDate) {
            userName = newName;
            userAge = newAge;
            userDateOfBirth = newBirthDate;
            lol = 1;
        }

        @DatabaseService.Column
        @DatabaseService.PrimaryKey
        public String userName;

        @DatabaseService.Column
        public int userAge;

        @DatabaseService.Column
        public Date userDateOfBirth;

        public int lol;
        @Override
        public String toString() {
            return "User{" +
                    "name='" + userName + '\'' +
                    ", age=" + userAge +
                    ", birthday=" + userDateOfBirth.toString() +
                    '}';
        }
    }

    @Test
    public void test() throws Exception {
        SimpleStudent user = new SimpleStudent("Alex", 18, new Date(System.currentTimeMillis()));

        DatabaseService<SimpleStudent> db = new DatabaseService<>(SimpleStudent.class);
//        db.dropTable();
        db.createTable();
        db.deleteById("Alex");
        db.insert(user);
//        db.update(user);

//        db.delete(user2.name);
        List<SimpleStudent> list = db.queryForAll();
        for (SimpleStudent u : list) {
            System.out.println(u.toString());
        }

//        System.out.println(db.queryById("Vasiya").toString());
    }
}