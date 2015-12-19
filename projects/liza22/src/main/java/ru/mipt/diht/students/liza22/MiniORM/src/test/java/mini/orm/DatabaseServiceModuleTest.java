package mini.orm;

import mini.orm.api.DatabaseService;
import mini.orm.model.CarEntity;
import mini.orm.model.IncorrectEntityWithoutColumnAnn;
import mini.orm.model.IncorrectEntityWithoutPrimaryKeyAnn;
import mini.orm.model.IncorrectEntityWithoutTableAnn;
import mini.orm.core.JdbcDatabaseService;
import org.h2.tools.Server;
import org.junit.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;

public final class DatabaseServiceModuleTest {
    private static final int N3 = 3;
    private static final int N5 = 5;
    private static final int N10 = 10;

    private DatabaseService<CarEntity, Integer> carDbService = new JdbcDatabaseService<>(CarEntity.class);

    @Before
    public void setUp() throws SQLException {
        // start DB server
        Server.createTcpServer("-tcpPort", "9001", "-tcpAllowOthers").start();
        // firstly create table
        carDbService.createTable();
    }

    @After
    public void setDown() throws SQLException {
        // drop table if it exists
        carDbService.dropTable();
        // stop DB server
        Server.shutdownTcpServer("tcp://localhost:9001", "", true, true);
    }

    @Test
    public void restAllDmlOperations() throws ParseException {
        // add data to table
        final CarEntity car1 = new CarEntity();
        car1.setId(1);
        car1.setName("Car#1");
        car1.setColor("green");
        car1.setTruck(false);
        car1.setCountOfDoors(N5);
        car1.setReleaseDate(new SimpleDateFormat("YYYY-MM-DD").parse("2010-12-01"));

        final CarEntity car2 = new CarEntity();
        car2.setId(2);
        car2.setName("Car#2");
        car2.setColor("blue");
        car2.setTruck(false);
        car2.setCountOfDoors(N3);
        car2.setReleaseDate(new SimpleDateFormat("YYYY-MM-DD").parse("2013-01-20"));

        final CarEntity truck = new CarEntity();
        truck.setId(N3);
        truck.setName("MegaTruck");
        truck.setColor("grey");
        truck.setTruck(true);
        truck.setCountOfDoors(2);
        truck.setReleaseDate(new SimpleDateFormat("YYYY-MM-DD").parse("2000-07-15"));

        carDbService.insert(car1);
        carDbService.insert(car2);
        carDbService.insert(truck);

        // select entity by primary key (car_2 expected)
        CarEntity carByPK = carDbService.queryById(2);
        assertNotNull(carByPK);
        assertEquals(car2, carByPK);

        CarEntity notExistedCar = carDbService.queryById(N10);
        assertNull(notExistedCar);

        // select all cars
        List<CarEntity> cars = carDbService.queryForAll();
        assertEquals(N3, cars.size());
        assertTrue(cars.contains(car1));
        assertTrue(cars.contains(car2));
        assertTrue(cars.contains(truck));

        // update any car
        CarEntity newTruck = new CarEntity();
        newTruck.setId(N3);
        newTruck.setName("New TheMegaTruck");
        newTruck.setColor("dark-grey");
        newTruck.setTruck(true);
        newTruck.setCountOfDoors(2);
        newTruck.setReleaseDate(new SimpleDateFormat("YYYY-MM-DD").parse("2015-07-15"));
        carDbService.update(newTruck);
        // select new truck and validate that it updated
        CarEntity newTruckSelected = carDbService.queryById(N3);
        assertEquals(newTruck, newTruckSelected);

        // delete cars
        carDbService.delete(car1);
        carDbService.delete(car2);
        // select all cars and check that deletion completed
        List<CarEntity> carsAfterDeletion = carDbService.queryForAll();
        assertEquals(1, carsAfterDeletion.size());
        assertTrue(carsAfterDeletion.contains(newTruck));

        carDbService.delete(newTruck);
        List<CarEntity> noCarsAnymore = carDbService.queryForAll();
        assertTrue(noCarsAnymore.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectEntityWithoutTableAnnotation() {
        DatabaseService<IncorrectEntityWithoutTableAnn, Integer> dbService =
                new JdbcDatabaseService<>(IncorrectEntityWithoutTableAnn.class);
        dbService.queryForAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectEntityWithoutColumnAnnotation() {
        DatabaseService<IncorrectEntityWithoutColumnAnn, Integer> dbService =
                new JdbcDatabaseService<>(IncorrectEntityWithoutColumnAnn.class);
        dbService.queryForAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectEntityWithoutPrimaryKeyAnnotation() {
        DatabaseService<IncorrectEntityWithoutPrimaryKeyAnn, Integer> dbService =
                new JdbcDatabaseService<>(IncorrectEntityWithoutPrimaryKeyAnn.class);
        dbService.queryForAll();
    }
}
