package ru.mipt.diht.students.maxdankow.miniorm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.List;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

public class Utils {

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

    public static <T> String getSqlValue(T object) {
        if (object.getClass() == String.class || object.getClass() == char.class) {
            return "\'" + object.toString() + "\'";
        } else {
            return object.toString();
        }
    }

    static public <T> T createItemFromSqlResult(ResultSet resultSet,
                                                List<ItemColumn> columnList,
                                                Class itemClass) throws SQLException {
        T newItem = null;
        try {
            // Создаем новый объект пустым конструктором.
            newItem = (T) itemClass.newInstance();

            // Перебираем все нужные столбцы-поля.
            for (ItemColumn column : columnList) {
                Field field = column.field;
                Field itemField = newItem.getClass().getField(field.getName());

                // Определяем какой тип получать в соостветствии с типом поля.
                // String
                if (field.getType() == String.class || field.getType() == char.class) {
                    String value = resultSet.getString(column.name);
                    field.set(newItem, value);
                }
                // int
                if (field.getType() == int.class || field.getType() == Integer.class) {
                    int value = resultSet.getInt(column.name);
                    field.set(newItem, value);
                }
                // float
                if (field.getType() == float.class || field.getType() == Double.class) {
                    float value = resultSet.getFloat(column.name);
                    field.set(newItem, value);
                }
                // boolean
                if (field.getType() == boolean.class) {
                    boolean value = resultSet.getBoolean(column.name);
                    field.set(newItem, value);
                }
                // Date
                if (field.getType() == Date.class) {
                    Date value = resultSet.getDate(column.name);
                    field.set(newItem, value);
                }
                // Time
                if (field.getType() == Time.class) {
                    Time value = resultSet.getTime(column.name);
                    field.set(newItem, value);
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return newItem;
    }
}
