package ru.mipt.diht.students.sopilnyak.miniorm;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class DatabaseServiceTest {

    @DatabaseService.Table
    public static class City {

        public City() {

        }

        public City(String cityName, int cityFoundationDate, long cityPopulation) {
            name = cityName;
            date = cityFoundationDate;
            population = cityPopulation;
        }

        @DatabaseService.PrimaryKey
        @DatabaseService.Column
        public String name;

        @DatabaseService.Column
        public int date;

        @DatabaseService.Column
        public long population;

        @Override
        public String toString() {
            return "Locality{" +
                    "name = \'" + name + "\'" +
                    ", foundation date = " + date +
                    ", population = " + population + "}";
        }
    }

    @Test
    public void testCreateDropTable() throws DatabaseException {
        DatabaseService<City> databaseService = new DatabaseService<>(City.class);

        databaseService.dropTable();
        databaseService.createTable();
        databaseService.dropTable();
    }

    @Test
    public void testInsertDelete() throws DatabaseException {
        DatabaseService<City> databaseService = new DatabaseService<>(City.class);

        databaseService.dropTable();
        databaseService.createTable();

        databaseService.insert(new City("Moscow", 1147, 12197596));
        databaseService.insert(new City("Elets", 1146, 105989));
        databaseService.insert(new City("Saint Petersburg", 1703, 5191690));
        databaseService.insert(new City("Kharkov", 1631, 1449674));

        databaseService.update(new City("Kharkov", 1630, 1449674));

        databaseService.delete("Saint Petersburg");
        databaseService.delete("Kharkov");

        //LinkedList<City> testList = new LinkedList<>();
        //testList.add(new City("Elets", 1146, 105989));
        //assertEquals(databaseService.queryById("Elets"), testList);
        assertEquals(databaseService.queryById("Kharkov"), new LinkedList<City>());
        assertEquals(databaseService.queryForAll().toString(), "[Locality{name = 'Moscow', "
                + "foundation date = 1147, population = 12197596}, "
                + "Locality{name = 'Elets', foundation date = 1146, population = 105989}]");

        databaseService.dropTable();
    }
}
