package ru.mipt.diht.students.maxdankow.miniorm;

public class DatabaseService<T> {
    private static final String DATABASE_PATH = "jdbc:h2:/tmp/simple_database";
    Class itemsClass;

    DatabaseService(Class newItemsClass) {
        itemsClass = newItemsClass;
    }

//    void insert(T item) {}
//    T queryById(K){}
//    T queryForAll(){};
//    void update(T item){}
//    void delete(T item) {}
//    void createTable() {}
//    void dropTable() {}
}
