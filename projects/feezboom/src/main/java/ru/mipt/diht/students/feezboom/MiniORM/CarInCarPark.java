package ru.mipt.diht.students.feezboom.MiniORM;

import ru.mipt.diht.students.feezboom.MiniORM.Annotations.Column;
import ru.mipt.diht.students.feezboom.MiniORM.Annotations.PrimaryKey;
import ru.mipt.diht.students.feezboom.MiniORM.Annotations.Table;

/**
 * * Created by avk on 18.12.15.
 **/


// Говорим, что у нас есть табличка, в которой строки будут элементами класса ниже.
// Соответственно каждой машине в таблице(на парковке) соответствует строка.
// В таблице колонки будут называться именно так, как указано в классе ниже.

@SuppressWarnings("checkstyle:visibilitymodifier")
@Table(name = "Parking")
public class CarInCarPark {
    @PrimaryKey
    @Column(name = "ID")
    // Идентификатор машины
    Integer id;

    @Column(name = "BRAND")
    // Марка автомобиля
    String brand;

    @Column(name = "OWNER")
    // Владелец автомобиля
    String owner;

    @SuppressWarnings("checkstyle:hiddenfield")
    CarInCarPark(Integer id, String brand, String owner) {
        this.id = id;
        this.brand = brand;
        this.owner = owner;
    }

    CarInCarPark() {
        this.id = 0;
        this.brand = "brand";
        this.owner = "owner";
    }

    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public String toString() {
        return "Car :\n"
                + "ID = " + id + "\n"
                + "Brand = " + brand + "\n"
                + "owner = " + owner;
    }
}


