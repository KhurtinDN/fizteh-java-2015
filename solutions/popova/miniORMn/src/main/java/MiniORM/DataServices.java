package MiniORM;

/**
 * Created by SM1 on 12.12.2015.
 */

    import java.lang.reflect.Field;
    import java.lang.reflect.InvocationTargetException;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.function.Function;

    public class DataServices<T> {
        private Class<T> Table_class;
        private String table_name;
        private Connection connection_test;
        private List<tColumn> columns;
        private tColumn PKeyColumn;

        private enum dType {
            INTEGER ("INT",Integer.class, Short.class),
            DOUBLE ("DOUBLE",Double.class),
            VARCHAR ("VARCHAR(150)",String.class);

            private String SQLtype;
            private final Class<?>[] jclass;

            dType(String sqlNameType, Class<?>... inst) {
                SQLtype = sqlNameType;
                jclass = inst;

            }


            private static dType valueOf(Class<?> class_type) throws Exception {
                for (dType type : values()) {
                    for (Class<?> exClass : type.jclass)
                      if (class_type.equals(exClass)) {
                            return type;
                        }
                    }
                throw new Exception("column must be one of supported types, but not "
                        + class_type.getSimpleName());
            }


        }

        private final class tColumn {
            private dType type;
            private String name;
            private Field field;

            private tColumn(Field realField) throws Exception {
                field = realField;
                field.setAccessible(true);
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation == null) {
                    throw new Exception("column field must be annotated");
                }
                name = columnAnnotation.name();
                if (name.equals("")) {
                    name = toSnakeCase(field.getName());
                }
                type = dType.valueOf(field.getType());

            }
        }

        private static String toSnakeCase(String camelCase) {
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 0; i < camelCase.length(); ++i) {
                if (camelCase.charAt(i) >= 'A' && camelCase.charAt(i) <= 'Z') {
                    if (i > 0 && camelCase.charAt(i - 1) >= 'a' && camelCase.charAt(i - 1) <= 'z') {
                        nameBuilder.append('_');
                    }
                    nameBuilder.append((char) (camelCase.charAt(i) - 'A' + 'a'));
                } else {
                    nameBuilder.append(camelCase.charAt(i));
                }
            }
            return nameBuilder.toString();
        }

        private String listColumns(Function<tColumn, String> convert) {
            StringBuilder values = new StringBuilder();
            for (tColumn column : columns) {
                values.append(convert.apply(column))
                        .append(',');
            }
            values.deleteCharAt(values.length() - 1);
            return values.toString();
        }

        public DataServices(Class<T> annotatedTableClass, String s, String s1, String s2) throws Exception {
            Table_class = annotatedTableClass;
            Table tableAnnotation = Table_class.getAnnotation(Table.class);
            if (tableAnnotation == null) {
                throw new Exception("table class must be annotated");
            }
            table_name = tableAnnotation.name();
            if (table_name.equals("")) {
                table_name = toSnakeCase(Table_class.getSimpleName());
            }
            Field[] allFields = Table_class.getDeclaredFields();
            columns = new ArrayList();
            for (Field field : allFields) {
                if (field.isAnnotationPresent(Column.class)) {
                    columns.add(new tColumn(field));
                    if (field.isAnnotationPresent(PrimaryKey.class)) {
                        PKeyColumn = columns.get(columns.size() - 1);
                    }
                }
            }
            try {
                connection_test = DriverManager.getConnection("jdbc:h2:~/testORM", "sa", "sa");
            } catch (SQLException e) {
                throw new Exception("Соединение не установлено: " + e.getMessage(), e);
            }
        }

        public final void createTable() throws Exception {
            StringBuilder columnsString = new StringBuilder();
            for (tColumn column : columns) {
                columnsString.append(column.name)
                        .append(' ')
                        .append(column.type.SQLtype);
                if (column.field.isAnnotationPresent(PrimaryKey.class)) {
                    columnsString.append(" NOT NULL PRIMARY KEY");
                }
                columnsString.append(',');
            }
            columnsString.deleteCharAt(columnsString.length() - 1); //remove last ','
            try {
                connection_test.createStatement().execute("CREATE TABLE IF NOT EXISTS " + table_name
                        + "(" + columnsString.toString() + ")");
            } catch (SQLException e) {
                throw new Exception("Таблица не создана: " + e.getMessage(), e);
            }
        }

        public final void dropTable() throws Exception {
            try {
                connection_test.createStatement().execute("DROP TABLE " + table_name);
            } catch (SQLException e) {
                throw new Exception("Таблица не удаляется: " + e.getMessage(), e);
            }
        }

        public final void insert(T row) throws Exception {
            try {
                PreparedStatement statement = connection_test.prepareStatement("INSERT INTO " + table_name
                        + " VALUES (" + listColumns(c -> "?") + ")");
                for (int i = 0; i < columns.size(); ++i) {
                    statement.setString(i + 1, String.valueOf(columns.get(i).field.get(row)));
                }
                statement.execute();
            } catch (Exception e) {
                throw new Exception("Строка не добавляется в таблицу: " + e.getMessage());
            }
        }

        public final void update(T row) throws Exception {
            if (PKeyColumn == null) {
                throw new Exception("Таблица должна иметь первичный ключ для указанной операции");
            }
            try {
                PreparedStatement statement = connection_test.prepareStatement("UPDATE " + table_name
                        + " SET " + listColumns(c -> c.name + "=?") + " WHERE " + PKeyColumn.name + "=?");
                for (int i = 0; i < columns.size(); ++i) {
                    statement.setString(i + 1, String.valueOf(columns.get(i).field.get(row)));
                }
                statement.setString(columns.size() + 1, PKeyColumn.field.get(row).toString());
                statement.execute();
            } catch (Exception e) {
                throw new Exception("Невозможно обновить строку: " + e.getMessage());
            }
        }

        public final void delete(T row) throws Exception {
            if (PKeyColumn == null) {
                throw new Exception("Таблица должна иметь первичный ключ для указанной операции");
            }
            try {
                PreparedStatement statement = connection_test.prepareStatement("DELETE FROM " + table_name
                        + " WHERE " + PKeyColumn.name + "=?");
                statement.setString(1, PKeyColumn.field.get(row).toString());
                statement.execute();
            } catch (Exception e) {
                throw new Exception("Строка не удаляется: " + e.getMessage());
            }
        }

        private List<T> getResult(ResultSet result) throws IllegalAccessException,
                SQLException, InstantiationException, InvocationTargetException, NoSuchMethodException {
            List<T> output = new ArrayList<>();
            while (result.next()) {
                T row = Table_class.newInstance();
                for (int i = 0; i < columns.size(); ++i) {
                    String value = result.getString(i + 1);
                    if (columns.get(i).field.getType().equals(String.class)) {
                        columns.get(i).field.set(row, value);
                    } else {
                        columns.get(i).field.set(row, columns.get(i).field.getType().getMethod("valueOf", String.class)
                                .invoke(null, value));
                    }
                }
                output.add(row);
            }
            result.close();
            return output;
        }

        public final <K> T queryById(K key) throws Exception {
            if (PKeyColumn == null) {
                throw new Exception("Таблица должна иметь первичный ключ для указанной операции");
            }
            if (!PKeyColumn.field.getType().isInstance(key)) {
                throw new Exception("Тип ключа не соответствует типу первичного ключа");
            }

            try {
                PreparedStatement statement = connection_test.prepareStatement("SELECT " + listColumns(c -> c.name)
                        + " FROM " + table_name + " WHERE " + PKeyColumn.name + "=?");
                statement.setString(1, key.toString());
                List<T> result = getResult(statement.executeQuery());
                if (result.isEmpty()) {
                    return null;
                }
                return result.get(0);
            } catch (Exception e) {
                throw new Exception("Невозможно получить строку: " + e.getMessage(), e);
            }
        }

        public final List<T> queryForAll() throws Exception {
            try {
                return getResult(connection_test.prepareStatement("SELECT * FROM " + table_name).executeQuery());
            } catch (Exception e) {
                throw new Exception("Не удалось получить строки: " + e.getMessage(), e);
            }
        }

        @Override
        protected final void finalize() {
            try {
                connection_test.close();
            } catch (SQLException e) {
                System.err.println("Соединение не закрывается: " + e.getMessage());
            }
        }
    }








