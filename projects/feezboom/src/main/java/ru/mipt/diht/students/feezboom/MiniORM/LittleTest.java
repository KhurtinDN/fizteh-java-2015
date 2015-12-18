package ru.mipt.diht.students.feezboom.MiniORM;

import java.util.List;

/**
 * * Created by avk on 18.12.15.
 **/
public class LittleTest {
    // Для тестиков
    public static void main(String[] args) throws Exception {
        DataBaseService<CarInCarPark> dataBaseService = new DataBaseService<>(CarInCarPark.class);

        dataBaseService.createTable();
        dataBaseService.insert(new CarInCarPark(1, "Toyota", "Vladimir"));
        dataBaseService.insert(new CarInCarPark(2, "Ford", "Alexander"));
        dataBaseService.insert(new CarInCarPark(3, "Nissan", "Alexey"));

        dataBaseService.update(new CarInCarPark(1, "BMW", "Andrew"));
        dataBaseService.delete(new CarInCarPark(1, "BMW", "Andrew"));
        List<CarInCarPark> answer =  dataBaseService.queryForAll();
        for (CarInCarPark car : answer) {
            System.out.println(car);
        }
        answer =  dataBaseService.queryForAll();
        for (CarInCarPark car : answer) {
            System.out.println(car);
        }

        CarInCarPark carInCarPark = dataBaseService.queryById(2);
        System.out.println("found by id :");
        System.out.println(carInCarPark);
        dataBaseService.dropTable();
    }
}
