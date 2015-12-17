package ru.mipt.diht.students.feezboom.MiniORM;

/**
 * * Created by avk on 17.12.15.
 **/
public class DataBaseService<T> {
    private Class<T> ourTableClass;

    DataBaseService(Class<T> inputClass) {
        ourTableClass = inputClass;
    }

    T queryById();
}
