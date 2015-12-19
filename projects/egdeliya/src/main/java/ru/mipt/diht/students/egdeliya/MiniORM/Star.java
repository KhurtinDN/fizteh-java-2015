package ru.mipt.diht.students.egdeliya.MiniORM;

import com.sun.nio.sctp.IllegalReceiveException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Эгделия on 19.12.2015.
 */

//таблица будет сожержать инфу о звёздах
//чтобы можно было делать переменные не private
@SuppressWarnings("checkstyle:visibilitymodifier")
@Table(name = "STARS")
public class Star {
    @PrimaryKey
    @Column(name = "ID")
    Integer id;

    @Column(name = "Name")
    String starName;

    @Column(name = "Radius")
    Integer radius;

    Star(Integer ourId, String n, Integer r) {
        starName = n;
        radius = r;
        id = ourId;
    }

    Star() {
        starName = "";
        radius = 0;
        id = 0;
    }

    public final String toString() {
        String result = starName + " " + radius + " " + id;
        return result;
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public static void main(String[] args) {
        DatabaseService<Star> dataBase = new DatabaseService<>(Star.class);
        try {

            dataBase.createTable();

            Star sun = new Star(0, "Sun", 60000);

            //insert
            System.out.println("\ninsert test");
            dataBase.insert(sun);
            dataBase.insert(new Star(1, "Alpha", 63000));
            dataBase.insert(new Star(2, "Betta", 28000));
            dataBase.insert(new Star(3, "Gamma", 15000));
            dataBase.insert(new Star(4, "Delta", 57900));
            dataBase.insert(new Star(5, "Omega", 1000));

            //SELECT *
            System.out.println("\nSelect all test");
            List<Star> table;
            table = dataBase.queryForAll();

            for (int i = 0; i < table.size(); i++) {
                System.out.println(table.get(i).toString());
            }

            //queryById
            System.out.println("\nqueryById test");
            try {
                Star myStar;
                myStar = dataBase.queryById(2);
                System.out.println(myStar);
            } catch (IllegalAccessException illegal) {
                System.out.println("IllegalAccessException in queryById");
                System.err.println(illegal.getMessage());
            } catch (SQLException sql) {
                System.err.println(sql.getMessage());
                System.out.println("SQLException in queryById");
            } catch (InstantiationException instal) {
                System.err.println(instal.getMessage());
                System.out.println("InstantiationException in queryById");
            }

            System.out.println("\ndelete test");
            //delete
            dataBase.delete(sun);

            //SELECT *
            List<Star> tableAfterDelete;
            tableAfterDelete = dataBase.queryForAll();

            for (int i = 0; i < tableAfterDelete.size(); i++) {
                System.out.println(tableAfterDelete.get(i));
            }

        } finally {
            dataBase.dropTable();
        }
    }
}


