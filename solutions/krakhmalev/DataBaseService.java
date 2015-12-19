import com.google.common.base.CaseFormat;
import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import ru.fizteh.fivt.students.krakhmalev.MiniORM.Annotations.Column;
import ru.fizteh.fivt.students.krakhmalev.MiniORM.Annotations.PrimaryKey;
import ru.fizteh.fivt.students.krakhmalev.MiniORM.Annotations.Table;

import javax.management.OperationsException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseService <T,K>{
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private Field primaryKey = null;
    private List<AnnotatedField> columns = null;
    private Class<T> typeClass = null;
    private String tableName = "";
    int primaryKeyIndex = -1;

    private boolean hasTableYet = false;

    private void connect() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName("org.h2.Driver").newInstance();
        connection = DriverManager.getConnection("jdbc:h2:test",
                "sa", "");
        statement = connection.createStatement();
    }

    protected void finalize ( ) throws SQLException {
        resultSet.close();
        statement.close();
        connection.close();
    }

    DataBaseService(Class<T> elementClass) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        try{
            this.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
        columns = new ArrayList<>();
        typeClass = elementClass;
        Table table = typeClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("Class should be annotated with Table");
        }
        tableName = table.name();
        if (tableName.equals("")) {
            tableName = "MY_TABLE";
        }
        int i = 0;
        for (Field elem : typeClass.getDeclaredFields()) {
            if (elem.getAnnotation(Column.class) != null) {
                columns.add(new AnnotatedField(elem));
            }
            if (elem.getAnnotation(PrimaryKey.class) != null) {
                if (elem.getAnnotation(Column.class) == null) {
                    throw new IllegalArgumentException("Первичный ключ должен иметь тип столбца");
                }
                if (primaryKey != null) {
                    throw new IllegalArgumentException("Первичный ключ долден быть один");
                }
                primaryKey = elem;
                primaryKeyIndex = i;
            }
            ++i;
        }
        try{
            resultSet = connection.getMetaData().getTables(null, null,
                            CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, tableName), null);
            if (resultSet.next()) {
                hasTableYet = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public T queryById(K id) throws OperationsException, SQLException, IllegalAccessException, InstantiationException {
        if (!hasTableYet){
            throw new OperationsException("Для данного запроа необходимо создать таблицу");
        }
        if (primaryKey == null){
            throw new OperationsException("Должен суущестовать первичный ключ");
        }
        if (!id.getClass().isInstance(primaryKey.getType())){
            throw new IllegalArgumentException("Ключ должен иметь тот же тип, что и первичные ключи таблицы");
        }
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("SELECT * FROM ").append(tableName).append(" WERE ")
                .append(columns.get(primaryKeyIndex).getColumnName()).append(" = ").append(id.toString());
        resultSet = statement.executeQuery(newRequest.toString());
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T item = typeClass.newInstance();
            for (AnnotatedField field: columns) {
                if (field.getElement().getType().equals(Integer.class)) {
                    field.getElement().set(item, resultSet.getInt(field.getColumnName()));
                }
                if (field.getElement().getType().equals(String.class)) {
                    field.getElement().set(item, resultSet.getString(field.getColumnName()));
                }
            }
            list.add(item);
        }
        if (list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new OperationsException("Ошибка получения результата");
        }
        return list.get(0);
    }

    List<T> queryForAll() throws OperationsException, SQLException, IllegalAccessException, InstantiationException {

        if (!hasTableYet){
            throw new OperationsException("Для данного запроа необходимо создать таблицу");
        }
        if (primaryKey == null){
            throw new OperationsException("Должен суущестовать первичный ключ");
        }
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("SELECT * FROM ").append(tableName);
        resultSet = statement.executeQuery(newRequest.toString());
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T item = typeClass.newInstance();
            for (AnnotatedField field: columns) {
                if (field.getElement().getType().equals(Integer.class)) {
                    field.getElement().set(item, resultSet.getInt(field.getColumnName()));
                }
                if (field.getElement().getType().equals(String.class)) {
                    field.getElement().set(item, resultSet.getString(field.getColumnName()));
                }
            }
            list.add(item);
        }
        return list;
    }

    void insert(T key) throws SQLException {
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("INSERT INTO").append(tableName).append(" (");
        boolean first = true;
        for (AnnotatedField field:columns){
            if (!first){
                newRequest.append(", ");
            }else {
                first = false;
            }
            newRequest.append(field.getColumnName());
        }
        statement.execute(newRequest.toString());

    }

    void update(T key) throws SQLException {
        StringBuilder newRequest = new StringBuilder().append("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < columns.size(); ++i) {
            if (i != 0) {
                newRequest.append(", ");
            }
            newRequest.append(columns.get(i).getColumnName()).append(" = ?");
        }
        newRequest.append(" WHERE ").append(columns.get(primaryKeyIndex).getColumnName())
                .append(" = ?");
        statement.execute(newRequest.toString());
    }


    void delete(T key) throws SQLException {
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("DELETE FROM ").append(tableName).append(columns.get(primaryKeyIndex).getColumnName()).append(" = ").append(key.toString());
        statement.execute(newRequest.toString());
    }

    void createTable() throws OperationsException, SQLException {
        if (hasTableYet) {
            throw new OperationsException("Невозможно повторно создать таблицу");
        }
        StringBuilder newRequest = new StringBuilder();
        newRequest.append("CREATE TABLE ").append(tableName).append(" (");
        int i = 0;
        for (AnnotatedField field : columns) {
            newRequest.append(field.getColumnName()).append(" ");
            newRequest.append(field.getH2Type()).append(" ");
            if (field.getElement().isAnnotationPresent(PrimaryKey.class)) {
                newRequest.append("NOT NULL PRIMARY KEY ");
                primaryKeyIndex = i;
            }
            i++;
            newRequest.append(" , ");
        }
        newRequest.append(") ");
        statement.execute(newRequest.toString());
    }

    void dropTable(){
        try {
            statement.execute("DROP TABLE " + tableName);
            hasTableYet = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
