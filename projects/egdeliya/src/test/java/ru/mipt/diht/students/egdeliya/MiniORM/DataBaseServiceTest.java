package ru.mipt.diht.students.egdeliya.MiniORM;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Эгделия on 19.12.2015.
 */

//
public class DataBaseServiceTest extends TestCase {
    DatabaseService<Star> dataBase;

    @Before
    public void initializer() {
        dataBase = new DatabaseService<>(Star.class);

        dataBase.createTable();
    }

    @Test
    public void testInsert() {
/*
        dataBase.insert(new Star("Sun", 60000, "Red"));
        dataBase.insert(new Star("Alpha", 63000, "Blue"));
        dataBase.insert(new Star("Betta", 28000, "Yellow"));
        dataBase.insert(new Star("Gamma", 15000, "Red"));
        dataBase.insert(new Star("Delta", 57900, "Blue"));
        dataBase.insert(new Star("Omega", 1000, "Orange"));
        */
    }

    @After
    public void cleaner() {
        dataBase.dropTable();
    }




}
