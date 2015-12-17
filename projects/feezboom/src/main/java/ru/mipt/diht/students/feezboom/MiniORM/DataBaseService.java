package ru.mipt.diht.students.feezboom.MiniORM;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * * Created by avk on 17.12.15.
 **/

public class DataBaseService<T> {
    private Class<T> ourTableClass;
    private List<T> columns;

    DataBaseService(Class<T> inputClass) throws Exception {
        tableInit(inputClass);
    }

    private void tableInit(Class<T> inputClass) throws Exception {
        ourTableClass = inputClass;
        // Хз чё
        Table tableAnnotation = ourTableClass.getAnnotation(Table.class);

        if (tableAnnotation == null) {
            throw new Exception("table must be annotated.");
        }

        // Видимо имя таблицы
        String tableName = tableAnnotation.name();
        if (tableName.equals("")) {
            tableName = "Vova";
        }
        // Видимо поля нашей таблицы
        Field[] allFields = ourTableClass.getDeclaredFields();

        // Дальше видимо заполняем эти поля
        columns = new ArrayList<>();


    }

    //T queryById();
}
