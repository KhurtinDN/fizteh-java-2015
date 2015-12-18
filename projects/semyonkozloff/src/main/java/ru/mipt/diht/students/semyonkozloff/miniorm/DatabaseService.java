package ru.mipt.diht.students.semyonkozloff.miniorm;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.CaseFormat;

import ru.mipt.diht.students.semyonkozloff.miniorm.annotation.Column;
import ru.mipt.diht.students.semyonkozloff.miniorm.annotation.PrimaryKey;
import ru.mipt.diht.students.semyonkozloff.miniorm.annotation.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class DatabaseService<K, V> {

    private Map<K, V> database;

    private String tableName;
    private List<String> columnNames = new ArrayList<>();

    private List<Field> columns = new ArrayList<>();
    private Field primaryKeyField = null;

    public DatabaseService(Class<V> servicedClass) {
        if (!servicedClass.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class must be "
                    + "annotated with \'@Table\'");
        }

        tableName = servicedClass.getAnnotation(Table.class).name();
        if (tableName.equals("")) {
            tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                    servicedClass.getSimpleName());
        }

        for (Field column : servicedClass.getDeclaredFields()) {
            if (!column.isAnnotationPresent(Column.class)) {
                throw new IllegalArgumentException("All fields must be "
                        + "annotated with \'@Column\'");
            }

            String columnName = column.getAnnotation(Column.class).name();
            if (columnName.equals("")) {
                columnName =
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                                column.getName());
            }

            if (column.isAnnotationPresent(PrimaryKey.class)) {
                if (primaryKeyField != null) {
                    throw new IllegalArgumentException("Primary key must be "
                            + "unique");
                } else {
                    primaryKeyField = column;
                }
            }

            columns.add(column);
            columnNames.add(columnName);
        }
    }

    @SuppressWarnings("unchecked")
    private K getPrimaryKey(V element) {
        try {
            return (K) primaryKeyField.get(element);
        } catch (IllegalAccessException exception) {
            return null;
        }
    }

    public V getById(K key) {
        return database.get(key);
    }

    public List<V> queryForAll() {
        return Lists.newArrayList(database.values());
    }

    public void insert(V element) {
        database.put(getPrimaryKey(element), element);
    }

    public void update(V element) {
        database.replace(getPrimaryKey(element), element);
    }

    public void delete(V element) {
        database.remove(getPrimaryKey(element));
    }

    public void createTable() {
        database = new TreeMap<>();
    }

    public void dropTable() {
        database.clear();
    }
}
