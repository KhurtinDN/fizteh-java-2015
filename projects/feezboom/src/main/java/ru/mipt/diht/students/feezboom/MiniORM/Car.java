package ru.mipt.diht.students.feezboom.MiniORM;

import ru.mipt.diht.students.feezboom.MiniORM.Annotations.Column;
import ru.mipt.diht.students.feezboom.MiniORM.Annotations.PrimaryKey;
import ru.mipt.diht.students.feezboom.MiniORM.Annotations.Table;

/**
 * * Created by avk on 18.12.15.
 **/

// У нас как бы есть класс Car
public class Car {
    @Table(name = "Parking")
    // Говорим, что у нас есть табличка, в которой строки будут элементами класса ниже.
    // Соответственно каждой машине в таблице(на парковке) соответствует строка - CarInfo.
    // В таблице колонки будут называться именно так, как указано в классе ниже.
    static class CarInfo {
        @PrimaryKey
        @Column(name = "ID")
        // Идентификатор машины
        private Integer id;

        @Column(name = "BRAND")
        // Марка автомобиля
        private String brand;

        @Column(name = "OWNER")
        // Владелец автомобиля
        private String owner;
    }
}
