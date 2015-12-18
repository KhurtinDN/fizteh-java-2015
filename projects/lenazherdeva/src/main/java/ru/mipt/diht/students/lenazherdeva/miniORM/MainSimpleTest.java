package ru.mipt.diht.students.lenazherdeva.miniORM;

import java.util.List;

/**
 * Created by admin on 19.12.2015.
 */
public class MainSimpleTest {
    @SuppressWarnings("checkstyle:magicnumber")
    public static void main(String[] args) throws Exception {
        DataBaseService<MarketsTable> dataBaseService = new DataBaseService<>(MarketsTable.class);
        dataBaseService.createTable();
        dataBaseService.insert(new MarketsTable(1, "Grocery", "Ivanov"));
        dataBaseService.insert(new MarketsTable(2, "Clothes", "Adamenko"));
        dataBaseService.insert(new MarketsTable(3, "Sweets", "Zhitlyhin"));

        dataBaseService.update(new MarketsTable(1, "Supermarket", "Champion"));
        dataBaseService.delete(new MarketsTable(1, "Superman", "Champion"));
        List<MarketsTable> answer =  dataBaseService.queryForAll();
        for (MarketsTable market : answer) {
            System.out.println(market);
        }
        answer =  dataBaseService.queryForAll();
        for (MarketsTable market : answer) {
            System.out.println(market);
        }

        MarketsTable marketsTable = dataBaseService.queryById(2);
        System.out.println("found by id :");
        System.out.println(marketsTable);
        dataBaseService.dropTable();
    }
}
