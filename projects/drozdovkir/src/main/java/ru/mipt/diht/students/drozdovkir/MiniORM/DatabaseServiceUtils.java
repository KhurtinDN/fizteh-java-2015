import javafx.util.Pair;

import ru.mipt.diht.students.drozdovkir.MiniORM.DatabaseService.Column;
import ru.mipt.diht.students.drozdovkir.MiniORM.DatabaseService.PrimaryKey;
import ru.mipt.diht.students.drozdovkir.MiniORM.DatabaseService.Table;

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
        Table tableAnnotation;
        if (itemClass.isAnnotationPresent(Table.class)) {
            tableAnnotation = (Table) itemClass.getAnnotation(Table.class);
        } else {
            throw new IllegalArgumentException("Class has no @Table annotation");
        }

        String tableName = tableAnnotation.name();
        if (Objects.equals(tableName, DatabaseService.UNNAMED)) {
            tableName = camelCaseToLowerCase(itemClass.getSimpleName());
        }
        return tableName;
    }

    public static Pair<List<TColumn>, TColumn> analyseColumns(Class itemClass) {
        List<TColumn> columnList = new ArrayList<>();
        TColumn primaryKey = null;

        Field[] fields = itemClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {

                Column column = field.getAnnotation(Column.class);
                String name = column.name();
                String type = column.type();


                if (name.equals(DatabaseService.UNNAMED)) {
                    name = camelCaseToLowerCase(field.getName());
                }
                TColumn itemColumn = new TColumn(name, type, field);
                columnList.add(itemColumn);

                if (field.isAnnotationPresent(PrimaryKey.class)) {
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
            newItem = (T) itemClass.newInstance();

            for (TColumn column : columnList) {
                Field field = column.getField();


                if (field.getType() == String.class || field.getType() == char.class) {
                    String value = resultSet.getString(column.getName());
                    field.set(newItem, value);
                }

                if (field.getType() == int.class || field.getType() == Integer.class) {
                    int value = resultSet.getInt(column.getName());
                    field.set(newItem, value);
                }

                if (field.getType() == float.class || field.getType() == Double.class) {
                    float value = resultSet.getFloat(column.getName());
                    field.set(newItem, value);
                }
                // boolean
                if (field.getType() == boolean.class) {
                    boolean value = resultSet.getBoolean(column.getName());
                    field.set(newItem, value);
                }

                if (field.getType() == Date.class) {
                    Date value = resultSet.getDate(column.getName());
                    field.set(newItem, value);
                }

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