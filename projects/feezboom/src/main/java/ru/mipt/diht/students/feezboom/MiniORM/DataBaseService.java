package ru.mipt.diht.students.feezboom.MiniORM;


import org.h2.jdbcx.JdbcConnectionPool;
import ru.mipt.diht.students.feezboom.MiniORM.Annotations.Column;
import ru.mipt.diht.students.feezboom.MiniORM.Annotations.PrimaryKey;
import ru.mipt.diht.students.feezboom.MiniORM.Annotations.Table;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * * Created by avk on 17.12.15.
 **/

@SuppressWarnings("checkstyle:designforextension")

// implements Closable потому что, наверное, нужно закрыть соединие, открытое
// с сервером, и, видимо, Closable сам его закрывает
// переопределённым методом (@Override) close
public class DataBaseService<T> implements Closeable {
    /* *****ПОЛЯ***** */
    // Поле нужное для соединения с сервером базы данных(H2)
    private JdbcConnectionPool connectionTool;

    // Имя таблицы
    private String tableName;

    // Поле, хранящее информацию о строках таблицы(элементах базы данных)
    private Class<T> ourTableClass;

    // Массив, содержащий имена колонок таблицы(имена полей элементов базы данных)
    private String[] namesOfColumns;

    // Массив, содержащий информациюю о полях элементов базы данных
    private Field[] fields;

    // Уникальный идентификатор элемента в базе данных
    private String primaryKeyFieldName;
    private int primaryKeyFieldNumber;
    /* *****ПОЛЯ***** */

    /* *******МЕТОДЫ******* */
    DataBaseService(Class<T> inputClass) throws Exception {
        // Это конструктор. Он вызывает инициализацию таблицы.
        tableInit(inputClass);
    }

    private void tableInit(Class<T> inputClass) throws Exception {
        // input class - это класс строки таблицы, в котором, к примеру, указана информация о полях
        // и столбцах которым, эти поля принаджелат
        // в общем это строка в той таблице, с которой мы будем работать
        ourTableClass = inputClass;
        // Таблица должна быть аннотирована, у нас она аннотирована в подпапке Annotations,
        // у неё есть поле - имя таблицы
        Table tableAnnotation = ourTableClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new Exception("Ваша таблица должна быть аннотирована.");
        }

        // Тут мы вытаскиваем имя таблицы:
        tableName = tableAnnotation.name();
        if (tableName.equals("")) {
            tableName = "Vova";
        }

        // Теперь мы должны вытащить информацию о полях нашей таблицы - вытащим поля из аннотации
        // Сделаем это:
        fields = ourTableClass.getDeclaredFields();

        // И создадим список имён этих столбцов, он нам пригодится
        List<String> columnsNamesList = new ArrayList<>();
        // Создавая этот список, будем одновременно проверять,
        // корректны ли входные данные - то есть все ли поля являются полями(поля - они же колонки в нашей таблице).
        int i = 0; // Это счётчик полей
        boolean present = false; // Проверка на присутствие primaryKey
        for (Field ourColumn : fields) {
            // Если столбец не соответствует аннотации столбца
             if (!ourColumn.isAnnotationPresent(Column.class)) {
                 throw new Exception("Не все поля вашего элемента таблицы являются столбцами.");
             }
            // Получим имя столбца через его аннотацию
            String currentColumnName = ourColumn.getAnnotation(Column.class).name();
            if (currentColumnName.equals("")) {
                currentColumnName = "EmptyName";
            }

            // Также проверить на то, что есть primary key, и он единственный
            // Если вдруг встретим поле с аннотацией PrimaryKey, то проверим,
            // встречается оно в первый раз или нет (переменная present)

            // Встретили поле с аннотацией PrimaryKey:
            if (ourColumn.isAnnotationPresent(PrimaryKey.class)) {
                if (!present) {
                    present = true;
                    primaryKeyFieldNumber = i;
                    primaryKeyFieldName = currentColumnName;
                } else {
                    throw new Exception("В таблице не должно присутствовать строк, "
                            + "имеющих более одного первичного ключа");
                }
            }

            columnsNamesList.add(currentColumnName); // Засунем имя столбца в список
            i++; // Считаем количество полей(столбцов) строк таблицы
        }
        // Запомним эти названия столбцов(поле класса)
        namesOfColumns = columnsNamesList.toArray(namesOfColumns);

        // Ну и последнне, что стоит сделать в инициализации -
        // это установить соединение с сервером, с которым
        // мы будем работать

        connectionTool = JdbcConnectionPool.create("connection_name",  "username", "password");
        //Инициализация - всё.
    }

    public void createTable() {
        // Здесь мы должны сконструировать запрос вида CREATE TABLE ...
        // Имя таблицы: tableName
        String ourSQLQuery = "CREATE TABLE IF NOT EXISTS " + tableName;
        ourSQLQuery += "(";
        for (int i = 0; i < fields.length; i++) {
            // Приписываем тип(преобразовывая в SQL синтаксис)
            ourSQLQuery += new FromJavaToSQLType(fields[i].getType());
            // Приписываем PRIMARY KEY, если нужен
            if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                ourSQLQuery += " PRIMARY KEY";
            }
            if (i != fields.length - 1) {
                ourSQLQuery += ",";
            }
        }
        ourSQLQuery += ")";

        // А дальше, отправляем этот запрос на сервер, чтобы,
        // соответственно, там создалась табличка, которая
        // нас и интересует.
        sendQuery(ourSQLQuery);
        // Всё.
    }

    public void dropTable() {
        // Составим SQL запрос удаления таблицы,
        // и отправим его на исполнение
        sendQuery("DROP TABLE IF EXISTS " + tableName);
    }

    public void insert(T elementToInsert) {
        // Сформируем запрос для вставки:
        String insertSQLQuery = "INSERT INTO TABLE " + tableName + "VALUES";
        insertSQLQuery += "(";
        // Нужно вставить все поля.
        for (int i = 0; i < fields.length; i++) {
            // Мы не можем точно определить что нужно ставить в качестве аргументов
            // в формируемом SQL запросе, поэтому мы ставим вопросики, которые позже
            // заменим на нужные нам выражения(с помощью класса java.sql.PreparedStatement)
            insertSQLQuery += "?";
            if (i != fields.length - 1) {
                insertSQLQuery += ",";
            }
        }
        insertSQLQuery += ")";

        // Подставим всё что нам нужно вместо вопросов:
        try {
            Connection connect = connectionTool.getConnection();
            PreparedStatement statement = connect.prepareStatement(insertSQLQuery);
            for (int i = 0; i < fields.length; i++) {
                // Текущее поле для подстановки
                Field currentField = fields[i];
                // Значение этого поля в объекте для вставки в базу данных.
                Object elementToSubstitute = currentField.get(elementToInsert);
                // Подставляем его вместо вопросика
                statement.setObject(i + 1, elementToSubstitute);
            }
            // И теперь просто исполняем запрос(посылаем на сервер).
            statement.execute();
        } catch (SQLException ex) {
            System.err.println("Возника ошибка SQL(запрос вставки).");
        } catch (IllegalAccessException ex) {
            System.err.println("Возникла ошибка неправомерного доступа.(запрос вставки)");
        }

    }

    public void update(T elementToUpdate) {
        // Здесь пробежимся по всем столбцам.
        for (int i = 0; i < namesOfColumns.length; i++) {
            String currentColumnName = namesOfColumns[i];
            // Формируем SQL запрос для обновления одного единственного поля(и так будет для каждого, по очереди)
            String updateSQLQuery = "UPDATE " + tableName + " SET"
                    + currentColumnName + " = ? WHERE " + primaryKeyFieldName + "= ?";
            // Подставим значения вместо вопросиков, точно так же как в insert()
            // и отправим запрос на выполнение
            try {
                Connection connect = connectionTool.getConnection();
                PreparedStatement statement = connect.prepareStatement(updateSQLQuery);
                // Подстановка вместо первого вопросика
                statement.setObject(1, fields[i].get(elementToUpdate));
                // Вместо второго вопросика
                statement.setObject(2, fields[primaryKeyFieldNumber].get(elementToUpdate));
                // Отправление на исполнение.
                statement.execute();
            } catch (SQLException ex) {
                System.err.println("Возника ошибка SQL(запрос обновления).");
            } catch (IllegalAccessException ex) {
                System.err.println("Возникла ошибка неправомерного доступа.(запрос обновления)");
            }
        }

    }

    public void delete(T elementToDelete) {
        // Формируем SQL запрос на удаление элемента
        String deleteSQLQuery = "DELETE FROM " + tableName + " WHERE "
                + primaryKeyFieldName + " = ?";
        // Далее просто подставляем вместо вопросиков то, что нужно, и отправляем на исполнение
        try {
            Connection connect = connectionTool.getConnection();
            PreparedStatement statement = connect.prepareStatement(deleteSQLQuery);
            // Подстановка вместо вопросика нужного нам поля
            statement.setObject(1, fields[primaryKeyFieldNumber].get(elementToDelete));
            // Отправка на исполнение.
            statement.execute();
        } catch (SQLException ex) {
            System.err.println("Возника ошибка SQL(запрос удаления).");
        } catch (IllegalAccessException ex) {
            System.err.println("Возникла ошибка неправомерного доступа(запрос удаления).");
        }
    }

    private void sendQuery(String querySQL) {
        try {
            // Установим соединение для отправки запроса
            Connection connection = connectionTool.getConnection();
            // И отправим его (execute)
            connection.createStatement().execute(querySQL);
        } catch (SQLException exception) {
            System.err.println("Ошибка SQL(отправка запроса).");
        }
    }

    public List<T> queryForAll() {
        // Возвращать будем список элементов таблицы
        // Запрос: SELECT * FROM TABLE
        String ourSQLQuery = "SELECT * FROM " + tableName;
        // Список с результатами запроса
        List<T> answerList = new ArrayList<>();
        try {
            // Устанавливаем соединение с сервером базы данных.
            Connection connection = connectionTool.getConnection();
            // Исполняем запрос. Получаем ответ.
            // Получаем ответ на наш запрос в виде класса ResultSet
            ResultSet baseResult = connection.createStatement().executeQuery(ourSQLQuery);

            // Теперь нужно вытащить данные из ResultSet:
            while (baseResult.next()) { // Пока есть что вытаскивать вытаскиваем.
                // Создадим элемент, в который будем сувать данные
                T ourTakenElement = ourTableClass.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    // Из baseResult данные мы можем вытаскивать только по имени столбца
                    // либо по номеру столбца, мы знаем имя(у нас есть массив имён полей),
                    // и, к тому же, мы должны заранее знать тип вытаскиваемых данных.
                    // Скажем, если класс fields[i] может быть присвоен Number'у, то вытащим его как Long
                    if (fields[i].getClass().isAssignableFrom(Number.class)) {
                        // Имя текущей колонки - fields[i]
                        String currentColumnName = namesOfColumns[i];
                        // Текущее значение, которое мы вытаскиваем из столбца с именем currentColumnName
                        Long currentValue = baseResult.getLong(currentColumnName);
                        // i-му полю нашего элемента присвоим вытащенное значение из ResultSet
                        fields[i].set(ourTakenElement, currentValue);
                    } else if (fields[i].getClass().isAssignableFrom(String.class)) {
                        // Аналогично поступим со String
                        String currentColumnName = namesOfColumns[i];
                        String currentValue = baseResult.getString(currentColumnName);
                        fields[i].set(ourTakenElement, currentValue);
                    } else if (fields[i].getClass().isAssignableFrom(Object.class)) {
                        // И аналогично поступим для Objects
                        String currentColumnName = namesOfColumns[i];
                        Object currentObject = baseResult.getObject(currentColumnName);
                        fields[i].set(ourTakenElement, currentObject);
                    }
                }
                answerList.add(ourTakenElement);
            }

        } catch (SQLException ex) {
            System.out.println("Словили исключение SQL (queryForAll) :" + ex);
        } catch (IllegalAccessException ex) {
            System.out.println("Ошибка неправомерного доступа (queryForAll): " + ex);
        } catch (InstantiationException ex) {
            System.out.println("Ошибка создания экземпляра класса (queryForAll) : " + ex);
        }

        return answerList;
    }

    public <K> T queryById(K primaryKey) throws Exception {
        // Уникальные ключи элементов базы данных могут быть любого типа, поэтому мы делаем ещё один шаблон
        // Запрос SELECT * FROM tableName WHERE ID = primaryKey
        // Делается практически также так и queryForAll
        String ourSQLQuery = "SELECT * FROM " + tableName + "WHERE " + primaryKeyFieldName + " = " + primaryKey;
        // Здесь мы запрашиваем ровно один элемент из таблицы, по его уникальному ключу, поэтому
        // тут не будет обработки результата в цикле, а просто единственный if
        try {
            // Устанавливаем соединение с сервером базы данных.
            Connection connection = connectionTool.getConnection();
            // Исполняем запрос. Получаем ответ.
            // Получаем ответ на наш запрос в виде класса ResultSet
            // Здесь он будет содержать единственный элемент, потому что мы запрашиваем
            // по уникальному ключу, соответствующему единственному элементу
            ResultSet baseResult = connection.createStatement().executeQuery(ourSQLQuery);

            // Теперь нужно вытащить данные из ResultSet:
            if (baseResult.next()) { // Пока есть что вытаскивать вытаскиваем.
                // Создадим элемент, в который будем сувать данные
                T ourTakenElement = ourTableClass.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    // Из baseResult данные мы можем вытаскивать только по имени столбца
                    // либо по номеру столбца, мы знаем имя.
                    // и, к тому же, мы должны заранее знать тип вытаскиваемых данных
                    // Скажем, что если класс fields[i] может быть присвоен Number'у, то вытащим его как Long
                    if (fields[i].getClass().isAssignableFrom(Number.class)) {
                        // Имя текущей колонки - fields[i]
                        String currentColumnName = namesOfColumns[i];
                        // Текущее значение, которое мы вытаскиваем из столбца с именем currentColumnName
                        Long currentValue = baseResult.getLong(currentColumnName);
                        // i-му полю нашего элемента присвоим вытащенное значение из ResultSet
                        fields[i].set(ourTakenElement, currentValue);
                    } else if (fields[i].getClass().isAssignableFrom(String.class)) {
                        // Аналогично поступим со String
                        String currentColumnName = namesOfColumns[i];
                        String currentValue = baseResult.getString(currentColumnName);
                        fields[i].set(ourTakenElement, currentValue);
                    } else if (fields[i].getClass().isAssignableFrom(Object.class)) {
                        // И аналогично поступим для Objects
                        String currentColumnName = namesOfColumns[i];
                        Object currentObject = baseResult.getObject(currentColumnName);
                        fields[i].set(ourTakenElement, currentObject);
                    }
                }
                // Вернём полученный результат
                return ourTakenElement;
            }

        } catch (SQLException ex) {
            System.out.println("Словили исключение SQL (queryById) :" + ex);
        } catch (IllegalAccessException ex) {
            System.out.println("Ошибка неправомерного доступа (queryById): " + ex);
        } catch (InstantiationException ex) {
            System.out.println("Ошибка создания экземпляра класса (queryById) : " + ex);
        }
        throw new Exception("Запрос по Id провалился (queryById).");
    }

    /* *******МЕТОДЫ******* */

    // Переопределённый метод класса Closable, от которого мы унаследовались
    // Закрывает соединение с базой данных.(сервером)
    @Override
    public void close() throws IOException {
        if (connectionTool != null) {
            connectionTool.dispose();
        }
    }
}

class FromJavaToSQLType {
    private String sqlClass;
    FromJavaToSQLType(Class javaClass) {
        if (javaClass == Integer.class) {
            sqlClass = "INTEGER";
        } else if (javaClass == String.class) {
            sqlClass = "VARCHAR(255)";
        }
    }
    @Override
    public String toString() {
        return sqlClass;
    }
}
