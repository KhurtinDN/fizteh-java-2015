package ru.mipt.diht.students.feezboom.MiniORM;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * * Created by avk on 19.12.15.
 **/
public class DataBaseServiceTest extends TestCase {
    DataBaseService<CarInCarPark> db;
    List<CarInCarPark> listForCheck = new ArrayList<>();

    @Before
    public void init() {
        db = new DataBaseService<>(CarInCarPark.class);

        db.createTable();
        db.insert(new CarInCarPark(0, "Toyota", "Andrey"));
        db.insert(new CarInCarPark(1, "Mitsubishi", "Vladimir"));
        db.insert(new CarInCarPark(2, "Ford", "Alexey"));
        db.insert(new CarInCarPark(3, "Audi", "Alexander"));
        db.insert(new CarInCarPark(4, "BMW", "Sergey"));
        db.insert(new CarInCarPark(5, "Volvo", "Dmitry"));
        db.insert(new CarInCarPark(6, "Opel", "Evgeniya"));
        db.insert(new CarInCarPark(7, "Honda", "Ivan"));
        db.insert(new CarInCarPark(8, "Lada", "Denis"));
        db.insert(new CarInCarPark(9, "Toyota", "Andrey"));


        listForCheck.add(new CarInCarPark(0, "Toyota", "Andrey"));
        listForCheck.add(new CarInCarPark(1, "Mitsubishi", "Vladimir"));
        listForCheck.add(new CarInCarPark(2, "Ford", "Alexey"));
        listForCheck.add(new CarInCarPark(9, "Toyota", "Andrey"));


        db.delete(new CarInCarPark(4, "Audi", "Alexander"));
        db.delete(new CarInCarPark(5, "Audi", "Alexander"));
        db.delete(new CarInCarPark(6, "Audi", "Alexander"));
        db.delete(new CarInCarPark(8, "Audi", "Alexander"));
        db.delete(new CarInCarPark(0, "Audi", "Alexander"));
        db.delete(new CarInCarPark(3, "Audi", "Alexander"));


    }

    @Test
    public void queryForAllAndDelete() {
        List<CarInCarPark> toCompare = db.queryForAll();

        for (CarInCarPark element: toCompare) {
            assertEquals(listForCheck.contains(element), true);
        }
    }

    @Test
    public void testQueryById() throws SQLException, InstantiationException, IllegalAccessException {
        CarInCarPark carInCarPark = db.queryById(2);
        assertEquals(carInCarPark.brand, "Ford");
        assertEquals(carInCarPark.owner, "Alexey");

        carInCarPark = db.queryById(0);
        assertEquals(carInCarPark.brand, "Toyota");
        assertEquals(carInCarPark.owner, "Andrey");
    }

    @After
    public void finish() {
        db.dropTable();
    }
}
