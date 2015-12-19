package ru.mipt.diht.students.tveritinova.MiniORM;

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
