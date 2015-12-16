package ru.mipt.diht.students.simon23rus.DatabaseService;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.simon23rus.DatabaseService.TClassAnnotations.Column;
import ru.mipt.diht.students.simon23rus.DatabaseService.TClassAnnotations.PrimaryKey;
import ru.mipt.diht.students.simon23rus.DatabaseService.TClassAnnotations.Table;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.rules.JunitRuleImpl;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by semenfedotov on 16.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabaseServiceTest extends TestCase {
    @Table
    public static class Player {
        @Column
        @PrimaryKey
        int id;

        @Column
        String lastName;
        @Column
        Long salary;
        @Column
        String club;

        Player() {}

        Player(String id, String lastName, String salary, String club) {
            this.id = Integer.valueOf(id);
            this.lastName = lastName;
            this.salary = Long.valueOf(salary);
            this.club = club;
        }

        Player(int id, String lastName, Long salary, String club) {
            this.id = id;
            this.lastName = lastName;
            this.salary = salary;
            this.club = club;
        }

        @Override
        public String toString() {
            return id + " | " + lastName + " | " + salary + " | " + club + " | ";
        }
    }

    @Test
    public void createTableTest() throws SQLException, ClassNotFoundException {
        //мб autoincrement?
        System.out.println("VSEM PEIVET");
        DatabaseService<Player> myFirstService = new DatabaseService<>(Player.class);
        Connection myFirstConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        StringBuilder dropper = new StringBuilder();
        myFirstConnection.createStatement().executeUpdate("DROP TABLE IF EXISTS PLAYER");
        myFirstService.createTable();
        DatabaseMetaData myMeta = myFirstConnection.getMetaData();
        ResultSet existence = myMeta.getTables(null, null, "PLAYER", null);
        if(existence.next()) {
            myFirstConnection.close();
            System.out.println("CREATED!");
            assert (true);
        }
        else {
            myFirstConnection.close();
            assert (false);
        }
    }

    @Test
    public void dropBoxTest() throws SQLException, ClassNotFoundException {
        DatabaseService<Player> mySecondService = new DatabaseService<>(Player.class);
        Connection mySecondConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        mySecondConnection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS player(id INTEGER NOT NULL PRIMARY KEY, last_name VARCHAR(20), salary BIGINT, club VARCHAR(20))");
        mySecondService.dropTable();
        DatabaseMetaData myMeta = mySecondConnection.getMetaData();
        ResultSet existence = myMeta.getTables(null, null, "player", null);
        if (existence.next()) {
            mySecondConnection.close();
            assert false;
        }
        else {
            mySecondConnection.close();
            assert true;
        }
    }

    @Test
    public void insertTest() throws SQLException, ClassNotFoundException, IllegalAccessException {
        DatabaseService<Player> myThirdService = new DatabaseService<>(Player.class);
        Connection myThirdCOnnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        System.out.println("UUU");
        myThirdService.createTable();
        List<Player> team = new ArrayList<>();
        team.add(new Player(1, "Ter Stegen", 55000L, "FCB"));
        team.add(new Player(2, "Douglas", 35000L, "FCB"));
        team.add(new Player(3, "Pique", 130000L,"FCB"));
        team.add(new Player(4, "Rakitic", 75000L, "FCB"));
        team.add(new Player(5, "Busquets", 120000L, "FCB"));
        team.add(new Player(6, "Dani Alves", 120000L, "FCB"));
        team.add(new Player(7, "David Villa", 100000L, "New York City"));
        team.add(new Player(8, "Iniesta", 150000L ,"FCB"));
        team.add(new Player(9, "Suarez", 200000L, "FCB"));
        team.add(new Player(10, "Messi", 256000L, "FCB"));
        team.add(new Player(11, "Neymar", 150000L, "FCB"));

        for(int i = 0; i < team.size(); ++i) {
            myThirdService.insert(team.get(i));
        }
        Statement selectZvezdochka = myThirdCOnnection.createStatement();
        ResultSet selectResult = selectZvezdochka.executeQuery("SELECT * FROM player");
        int rowNumber = 0;
        List<Player> added = new ArrayList<>();
        while (selectResult.next()) {
            added.add(new Player(
                    selectResult.getString(1),
                    selectResult.getString(2),
                    selectResult.getString(3),
                    selectResult.getString(4))
            );
            ++rowNumber;
        }
        assertEquals(11, rowNumber);
        for(int i = 0; i < rowNumber; ++i) {
            assertEquals(team.get(i).toString(), added.get(i).toString());
        }
        myThirdCOnnection.close();
    }

    @Test
    public void updateTest() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        DatabaseService<Player> myFourthService = new DatabaseService<>(Player.class);
        Connection myFourthConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        Player updater = new Player(7, "Sanek", 123456L, "FC Vodnik");
        myFourthService.update(updater);
        Player result = myFourthService.queryById(7);
        System.out.println(result.toString());
        assertEquals(updater.toString(), result.toString());
        myFourthConnection.close();
    }

    @Test
    public void deleteTest() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        DatabaseService<Player> myFourthService = new DatabaseService<>(Player.class);
        Connection myFourthConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        Player toDelete = new Player(1, "Ter Stegen", 55000L, "FCB");
        myFourthService.delete(toDelete);
        assertEquals(null, myFourthService.queryById(1));
        myFourthConnection.close();
    }

    @Test
    public void queryByIdTest() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DatabaseService<Player> myFourthService = new DatabaseService<>(Player.class);
        Connection myFourthConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        int key = 4;
        assertEquals(new Player(4, "Rakitic", 75000L, "FCB").toString(), myFourthService.queryById(key).toString());
        myFourthConnection.close();
    }

    @Test
    public void queryAllTest() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        DatabaseService<Player> myFourthService = new DatabaseService<>(Player.class);
        Connection myFourthConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        Statement selectZvezdochka = myFourthConnection.createStatement();
        System.out.println("We R Here");
        ResultSet selectResult = selectZvezdochka.executeQuery("SELECT * FROM player");
        List<Player> answer = new ArrayList<>();
        while (selectResult.next()) {
            answer.add((new Player(
                    selectResult.getString(1),
                    selectResult.getString(2),
                    selectResult.getString(3),
                    selectResult.getString(4))
            ));
        }
        List<Player> team = new ArrayList<>();
        team.add(new Player(2, "Douglas", 35000L, "FCB"));
        team.add(new Player(3, "Pique", 130000L,"FCB"));
        team.add(new Player(4, "Rakitic", 75000L, "FCB"));
        team.add(new Player(5, "Busquets", 120000L, "FCB"));
        team.add(new Player(6, "Dani Alves", 120000L, "FCB"));
        team.add(new Player(7, "Sanek", 123456L, "FC Vodnik"));
        team.add(new Player(8, "Iniesta", 150000L ,"FCB"));
        team.add(new Player(9, "Suarez", 200000L, "FCB"));
        team.add(new Player(10, "Messi", 256000L, "FCB"));
        team.add(new Player(11, "Neymar", 150000L, "FCB"));
        assertEquals(team.size(), answer.size());
        for(int i = 0; i < team.size(); ++i) {
            assertEquals(team.get(i).toString(), answer.get(i).toString());
        }
//        List<Player> selectZvezdochkaResult = myFourthService.queryForAll();
//        System.out.println(selectZvezdochkaResult.toString());
    }
}

