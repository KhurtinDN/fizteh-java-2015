package ru.mipt.diht.students.simon23rus.DatabaseService;

import com.google.common.base.CaseFormat;
import javax.activation.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ru.mipt.diht.students.simon23rus.DatabaseService.TClassAnnotations.*;
/**
 * Created by semenfedotov on 15.12.15.
 */
public class DatabaseService<T> {
    private DataSource dataBase;
    private String dataBaseName;
    private List<String> columnNames;
    private List<String> sQLClasses;
    private Class<T> givenClass;
    private Field[] allFields;
    private StringBuilder allColumnNames;
    private String myTableName;
    private Field primaryKey;
    private int primaryKeyPos = -1;
    private boolean isCreated = false;

    public final DataSource getDataBase() {
        return dataBase;
    }

    public final String getDataBaseName() {
        return dataBaseName;
    }

    public final List<String> getColumnNames() {
        return columnNames;
    }

    public final List<String> getsQLClasses() {
        return sQLClasses;
    }

    public final Class<T> getGivenClass() {
        return givenClass;
    }

    public final StringBuilder getAllColumnNames() {
        return allColumnNames;
    }

    public final Field[] getAllFields() {
        return allFields;
    }

    public final String getMyTableName() {
        return myTableName;
    }

    public final Field getPrimaryKey() {
        return primaryKey;
    }

    public final int getPrimaryKeyPos() {
        return primaryKeyPos;
    }

    DatabaseService(Class<T> givenClass) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        SQLTypeConverter myOwnConverter = new SQLTypeConverter();
        columnNames = new ArrayList<String>();
        sQLClasses = new ArrayList<String>();
        this.givenClass = givenClass;
        myTableName = givenClass.getAnnotation(Table.class).name();
        allFields = givenClass.getDeclaredFields();
        if (myTableName.equals("")) {
            //default name
            //getSimpleName vozvraschaet name bez prefixa s packagami
            //t.k nam dano s UpperCamel, perevedem v lower s podcherkivaniem with CaseFormat by google
            myTableName = givenClass.getSimpleName();
            myTableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, myTableName);
        }
        dataBaseName = myTableName;

            int index = 0;
            for (Field givenField : givenClass.getDeclaredFields()) {
                if (givenField.getAnnotation(PrimaryKey.class) != (null)) {
                    if (givenField.getAnnotation(Column.class) == (null)) {
                        System.out.println("error with primary key");
                    }
                    primaryKey = givenField;
                    primaryKeyPos = index;
                }
                if (!givenField.getAnnotation(Column.class).equals(null)) {
                    System.out.println(givenField.getType());
                    System.out.println(givenField.getName());
                    sQLClasses.add(myOwnConverter.convertToSQLType(givenField.getType()));
                    columnNames.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, givenField.getName()));
                }
                ++index;
            }
        Connection myFirstConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        //proverim suschestvuet li tablica
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, dataBaseName) + "SECRET");
        ResultSet answer = myFirstConnection.getMetaData().getTables(null, null, dataBaseName, null);
        if (answer.next()) {
            isCreated = true;
        }
        myFirstConnection.close();
    }

    public List<T> resultSetHandler(ResultSet result) throws SQLException, IllegalAccessException, InstantiationException {
        List<T> answer = new ArrayList<>();
        while (result.next()) {
            T currentElement = givenClass.newInstance();
            for(int i = 0; i < columnNames.size(); ++i) {
                String currentSQLType = sQLClasses.get(i);
                Class currentClass = allFields[i].getType();
                if(currentSQLType == "VARCHAR(20)") {
                    if(currentClass.equals(String.class)) {
                        String toSet = result.getString(i + 1);
                        allFields[i].set(currentElement, toSet);
                    }
                    else {
                        char toSet = result.getString(i + 1).charAt(0);
                        allFields[i].set(currentElement, toSet);
                    }
                }
                else if(currentSQLType == "INTEGER") {
                    Integer toSet = result.getInt(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "BIGINT") {
                    Long toSet = result.getLong(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "TINYINT") {
                    byte toSet = result.getByte(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "BOOLEAN") {
                    boolean toSet = result.getBoolean(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "DOUBLE") {
                    Double toSet = result.getDouble(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "REAL") {
                    float toSet = result.getFloat(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "DATE") {
                    Date toSet = result.getDate(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "TIME") {
                    Time toSet = result.getTime(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else if(currentSQLType == "ARRAY") {
                    Array toSet = result.getArray(i + 1);
                    allFields[i].set(currentElement, toSet);
                }
                else {
                    System.out.println("Your class is non-supported");
                }
            }
            answer.add(currentElement);
        }
        return answer;
    }

    public <K> T queryById(K thisPrimaryKey) throws SQLException, IllegalAccessException, InstantiationException {
        if(primaryKey == null) {
            System.out.println("There is no primary Key in the Table");
        }
        Connection queryConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(dataBaseName).append(" WHERE ").append(columnNames.get(primaryKeyPos)).append("=?");
        PreparedStatement queryStmt = queryConnection.prepareStatement(query.toString());
        queryStmt.setString(1, thisPrimaryKey.toString());
        ResultSet result = queryStmt.executeQuery();
        List<T> answer = resultSetHandler(result);
        queryConnection.close();
        if(answer.size() == 0) {
            System.out.println("Error while gettting query by primKey AnswerSize is null");
            return null;
        }
        else {
            return answer.get(0);
        }

    }
    List<T> queryForAll() throws SQLException, InstantiationException, IllegalAccessException {
        Connection queryConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(dataBaseName);
        List<T> queryResult = resultSetHandler(queryConnection
                .createStatement()
                .executeQuery(query.toString()));
        queryConnection.close();
        return queryResult;
    }
    void insert(T toInsert) throws SQLException, IllegalAccessException {
        Connection insertingConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        StringBuilder buildValues = new StringBuilder();
        buildValues.append("INSERT INTO ").append(dataBaseName).append(" VALUES (");
        for (int i = 0; i < columnNames.size(); ++i) {
            buildValues.append("?");
            if (i != columnNames.size() - 1) {
                buildValues.append(", ");
            }
        }
        buildValues.append(")");
        PreparedStatement insertionStatement = insertingConnection.prepareStatement(buildValues.toString());
        for (int i = 0; i < columnNames.size(); ++i) {
            insertionStatement.setObject(i + 1, allFields[i].get(toInsert));
        }
        int diff = insertionStatement.executeUpdate();
        assert (diff != 0);
        insertingConnection.close();
    }
    void update(T toUpdate) throws SQLException, IllegalAccessException {
        Connection updateConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        StringBuilder updateValues = new StringBuilder();
        updateValues.append("UPDATE ").append(dataBaseName).append(" SET ");
        for (int i = 0; i < columnNames.size(); ++i) {
            updateValues.append(columnNames.get(i)).append("=?");
            if (i != columnNames.size() - 1) {
                updateValues.append(", ");
            }
        }
        updateValues.append(" WHERE ").append(columnNames.get(primaryKeyPos)).append("=?");
        PreparedStatement updateStmt = updateConnection.prepareStatement(updateValues.toString());
        for (int i = 0; i < columnNames.size(); ++i) {
            updateStmt.setObject(i + 1, allFields[i].get(toUpdate));
        }
        updateStmt.setObject(columnNames.size() + 1, allFields[primaryKeyPos].get(toUpdate));
        int diff = updateStmt.executeUpdate();
        assert (diff != 0);
        updateConnection.close();
    }
    void delete(T toDelete) throws SQLException, IllegalAccessException {
        Connection deletionConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        StringBuilder deleteValues = new StringBuilder();
        deleteValues.append("DELETE FROM ")
                .append(dataBaseName)
                .append(" WHERE ")
                .append(columnNames.get(primaryKeyPos))
                .append("=?");
        PreparedStatement deletionStmt = deletionConnection.prepareStatement(deleteValues.toString());
        deletionStmt.setObject(1, allFields[primaryKeyPos].get(toDelete));
        int diff = deletionStmt.executeUpdate();
        assert (diff != 0);
        deletionConnection.close();
    }
    void createTable() throws SQLException, ClassNotFoundException {
        if(isCreated) {
            System.out.println("Table has already created!");
            return;
        }
        Connection creatingConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        Statement creatingStatement = creatingConnection.createStatement();
        StringBuilder creatingQuery = new StringBuilder();
        creatingQuery.append("CREATE TABLE IF NOT EXISTS ")
        .append(dataBaseName).append("(");
        System.out.println(dataBaseName);
        for (int i = 0; i < columnNames.size(); ++i) {
            creatingQuery.append(columnNames.get(i)).append(" ").append(sQLClasses.get(i));
            if (i == primaryKeyPos) {
                creatingQuery.append(" NOT NULL PRIMARY KEY");
            }
            if (i != columnNames.size() - 1) {
                creatingQuery.append(", ");
            }
        }
        creatingQuery.append(")");
        System.out.println(creatingQuery.toString());
        int diff = creatingStatement.executeUpdate(creatingQuery.toString());
        isCreated = true;
        creatingConnection.close();
    }
    void dropTable() throws SQLException {
        if (!isCreated) {
            System.out.println("Dropping of non-existing table");
            return;
        }
        Connection droppingConnection = DriverManager.getConnection("jdbc:h2:./dbTask");
        Statement droppingStatement = droppingConnection.createStatement();
        StringBuilder droppingQuery = new StringBuilder();
        droppingQuery.append("DROP TABLE ").append(dataBaseName);
        int diff = droppingStatement.executeUpdate(droppingQuery.toString());
        droppingConnection.close();
    }

    public static void main(String[] args) {
        List<String> results = new ArrayList<String>();
        results.add("dsad");
        results.add("fsdqwe");
        System.out.println(results.toString());
    }
}
