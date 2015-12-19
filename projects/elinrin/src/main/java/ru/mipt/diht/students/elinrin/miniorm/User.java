package ru.mipt.diht.students.elinrin.miniorm;

/*
import ru.mipt.diht.students.elinrin.miniorm.annotations.Column;
import ru.mipt.diht.students.elinrin.miniorm.annotations.PrimaryKey;
import ru.mipt.diht.students.elinrin.miniorm.annotations.Table;
import ru.mipt.diht.students.elinrin.miniorm.exception.HandlerOfException;

import javax.management.OperationsException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
*/


public class User {
    /*@Table(name = "TESTTABLE")
    static class Tab {
        @PrimaryKey
        @Column(name = "ID")
        Integer a;

        @Column(name = "STRING")
        String s;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Tab) {
                return ((Objects.equals(this.a, ((Tab) obj).a)) && (Objects.equals(this.s, ((Tab) obj).s)));
            }
            return false;
        }

        Tab(Object a, Object s) {
            this.a = (Integer) a;
            this.s = (String) s;
        }

        Tab() {
            this.a = 0;
            this.s = "";
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder().append("Id = ").append(a).append(", String = ").append(s);
            return result.toString();
        }
    }

    public static void main(String[] argv) {
        DatabaseService<Tab> bd = null;
        try {
            bd = new DatabaseService(Tab.class);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            HandlerOfException.handler(e);
        }

        try {
            bd.createTable();
            bd.insert(new Tab(1, "one"));
            List<Tab> all = bd.queryForAll();
            all.forEach(System.out::println);

            bd.insert(new Tab(2, "two"));
            bd.insert(new Tab(3, "three"));
            bd.insert(new Tab(4, "four"));

            Tab elem = bd.queryById(3);
            System.out.println(elem);

            all = bd.queryForAll();
            all.forEach(System.out::println);
        } catch (IllegalAccessException | InstantiationException | OperationsException | SQLException e) {
            HandlerOfException.handler(e);
        } finally {
            bd.dropTable();
        }
    }*/
}
