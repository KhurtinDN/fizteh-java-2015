package ru.mipt.diht.students.glutolik.MiniORM;

import javafx.util.Pair;

import ru.mipt.diht.students.glutolik.MiniORM.DatabaseService.Column;
import ru.mipt.diht.students.glutolik.MiniORM.DatabaseService.PrimaryKey;
import ru.mipt.diht.students.glutolik.MiniORM.DatabaseService.Table;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

/**
 * Created by glutolik on 18.12.15.
 */
public class DatabaseServiceUtils {
    public static String camelCaseToLowerCase(String text) {
        StringBuilder lowerCaseText = new StringBuilder("");

        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (isUpperCase(ch)) {
                if (i != 0) {
                    lowerCaseText.append("_");
                }
                lowerCaseText.append(toLowerCase(ch));
            } else {
                lowerCaseText.append(ch);
            }
        }
        return lowerCaseText.toString();
    }

    public static String getTableName(Class itemClass) throws IllegalArgumentException {
        // Проверяем, проаннотирован ли класс @Table
        Table tableAnnotation;
        if (itemClass.isAnnotationPresent(Table.class)) {
            tableAnnotation = (Table) itemClass.getAnnotation(Table.class);
        } else {
            throw new IllegalArgumentException("Class has no @Table annotation");
        }

        // Если имя таблицы не указано, то сгерерируем его.
        String tableName = tableAnnotation.name();
        if (Objects.equals(tableName, DatabaseService.UNNAMED)) {
            tableName = camelCaseToLowerCase(itemClass.getSimpleName());
        }
        return tableName;
    }

    public static Pair<List<TColumn>, TColumn> analyseColumns(Class itemClass) {
        List<TColumn> columnList = new ArrayList<>();
        TColumn primaryKey = null;

        // Пройдемся по полям класса и найдем аннотированные @Column
        Field[] fields = itemClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {

                Column column = field.getAnnotation(Column.class);
                String name = column.name();
                String type = column.type();

                // Если имя не задано, то сгернерируем.
                if (name.equals(DatabaseService.UNNAMED)) {
                    name = camelCaseToLowerCase(field.getName());
                }
                TColumn itemColumn = new TColumn(name, type, field);
                columnList.add(itemColumn);

                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    // Объявление более одного @PrimaryKey недопустимо.
                    if (primaryKey != null) {
                        throw new IllegalArgumentException("More than one primary key presents");
                    }
                    primaryKey = itemColumn;
                }
            }
        }
        return new Pair<List<TColumn>, TColumn>(columnList, primaryKey);
    }

    public static <T> String getSqlValue(T object) {
        if (object.getClass() == String.class || object.getClass() == char.class) {
            return "\'" + object.toString() + "\'";
        } else {
            return object.toString();
        }
    }

    public static <T> T createItemFromSqlResult(ResultSet resultSet,
                                                List<TColumn> columnList,
                                                Class itemClass) throws SQLException {
        T newItem = null;
        try {
            // Создаем новый объект пустым конструктором.
            newItem = (T) itemClass.newInstance();

            // Перебираем все нужные столбцы-поля.
            for (TColumn column : columnList) {
                Field field = column.getField();

                // Определяем какой тип получать в соостветствии с типом поля.
                // String
                if (field.getType() == String.class || field.getType() == char.class) {
                    String value = resultSet.getString(column.getName());
                    field.set(newItem, value);
                }
                // int
                if (field.getType() == int.class || field.getType() == Integer.class) {
                    int value = resultSet.getInt(column.getName());
                    field.set(newItem, value);
                }
                // float
                if (field.getType() == float.class || field.getType() == Double.class) {
                    float value = resultSet.getFloat(column.getName());
                    field.set(newItem, value);
                }
                // boolean
                if (field.getType() == boolean.class) {
                    boolean value = resultSet.getBoolean(column.getName());
                    field.set(newItem, value);
                }
                // Date
                if (field.getType() == Date.class) {
                    Date value = resultSet.getDate(column.getName());
                    field.set(newItem, value);
                }
                // Time
                if (field.getType() == Time.class) {
                    Time value = resultSet.getTime(column.getName());
                    field.set(newItem, value);
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newItem;
    }
}

