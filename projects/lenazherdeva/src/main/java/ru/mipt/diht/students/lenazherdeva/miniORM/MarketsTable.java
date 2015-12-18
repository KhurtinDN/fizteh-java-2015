package ru.mipt.diht.students.lenazherdeva.miniORM;

import ru.mipt.diht.students.lenazherdeva.miniORM.annotations.Column;
import ru.mipt.diht.students.lenazherdeva.miniORM.annotations.PrimaryKey;
import ru.mipt.diht.students.lenazherdeva.miniORM.annotations.Table;

/**
 * Created by admin on 19.12.2015.
 */

    @Table(name = "Items in markets")
    public class MarketsTable {
    @PrimaryKey

    @Column(name = "ID")
    private Integer id;


    @Column(name = "TYPE")
    private String typeOfItem;

    @Column(name = "OWNER")
    private String owner;

    MarketsTable(Integer inputId, String type, String inputOwner) {
        this.id = inputId;
        this.typeOfItem = type;
        this.owner = inputOwner;
    }
    MarketsTable() { }


    @Override
    public final String toString() {
        return "Car :\n"
                + "ID = " + id + "\n"
                + "Brand = " + typeOfItem + "\n"
                + "owner = " + owner;
    }
}
