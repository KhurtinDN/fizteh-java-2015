package ru.mipt.diht.students.IrinaMudrova.MiniOrm;

import ru.mipt.diht.students.IrinaMudrova.MiniOrm.annotations.Column;
import ru.mipt.diht.students.IrinaMudrova.MiniOrm.annotations.PrimaryKey;
import ru.mipt.diht.students.IrinaMudrova.MiniOrm.annotations.Table;
import ru.mipt.diht.students.IrinaMudrova.MiniOrm.exceptions.*;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.CaseFormat;

public class DataConverter<T> {
    public String getTableName() {
        return tableName;
    }

    public Field getPrimaryKeyField() {
        return primaryKeyField;
    }

    public List<FieldConverter> getFields() {
        return fields;
    }

    class FieldConverter {
        private String name;
        private Field field;

        public FieldConverter(String name, Field field) {
            this.name = name;
            this.field = field;
        }

        public String getName() {
            return name;
        }

        public Field getField() {
            return field;
        }

        public String type() throws NotImplementedTypesException {
            if (field.getType().equals(String.class)) {
                return "varchar(255)";
            }
            if (field.getType().equals(Integer.class)) {
                return "int";
            }
            throw new NotImplementedTypesException();
        }

        public String stringValueOf(T item) throws NotImplementedTypesException {
            try {
                if (field.getType().equals(String.class)) {
                    return "\"" + (String) field.get(item) + "\"";
                }
                if (field.getType().equals(Integer.class)) {
                    return "" + (Integer) field.get(item);
                }
            } catch (Exception e) {
                throw new InternalDatabaseError();
            }
            throw new NotImplementedTypesException();
        }

        public String generateDeclaration() throws NotImplementedTypesException {
            if (field == primaryKeyField) {
                return name + " " + type() + " primary key";
            } else {
                return name + " " + type();
            }
        }
    }

    private List<FieldConverter> fields = null;
    private Field primaryKeyField = null;
    private FieldConverter primaryKeyFieldConverter = null;
    private String tableName;

    protected String stringValueOf(Object x) throws NotImplementedTypesException {
        try {
            if (x.getClass().equals(String.class)) {
                return "\"" + (String) x + "\"";
            }
            if (x.getClass().equals(Integer.class)) {
                return "" + (Integer) x;
            }
        } catch (Exception e) {
            throw new InternalDatabaseError();
        }
        throw new NotImplementedTypesException();
    }


    public String generateCreateCommand() throws NotImplementedTypesException {
        StringBuilder builder = new StringBuilder().append("create table ").append(tableName).append(" (");
        for (int i = 0; i + 1 < fields.size(); i++) {
            builder.append(fields.get(i).generateDeclaration());
            builder.append(", ");
        }
        builder.append(fields.get(fields.size() - 1).generateDeclaration());
        builder.append(")");
        return builder.toString();
    }

    public String generateDropCommand() {
        return "drop table " + tableName;
    }

    public String generateInsertCommand(T item) throws NotImplementedTypesException {
        StringBuilder builder = new StringBuilder().append("insert into ").append(tableName).append(" values (");
        for (int i = 0; i + 1 < fields.size(); i++) {
            builder.append(fields.get(i).stringValueOf(item));
            builder.append(", ");
        }
        builder.append(fields.get(fields.size() - 1).stringValueOf(item));
        builder.append(")");
        return builder.toString();
    }

    public String generateUpdateCommand(T item) throws NotImplementedTypesException {
        StringBuilder builder = new StringBuilder().append("update ").append(tableName).append(" set ");
        for (int i = 0; i + 1 < fields.size(); i++) {
            builder.append(fields.get(i).getName()).append(" = ")
                    .append(fields.get(i).stringValueOf(item)).append(", ");
        }
        builder.append(fields.get(fields.size() - 1).getName()).append(" = ")
                .append(fields.get(fields.size() - 1).stringValueOf(item));
        builder.append(" where ");
        builder.append(primaryKeyFieldConverter.getName()).append(" = ")
                .append(primaryKeyFieldConverter.stringValueOf(item));
        return builder.toString();
    }

    public String generateDeleteCommand(T item) throws NotImplementedTypesException {
        StringBuilder builder = new StringBuilder().append("delete from ").append(tableName).append(" where ");
        return "delete from " + tableName + " where " + primaryKeyFieldConverter.getName() + " = "
                + primaryKeyFieldConverter.stringValueOf(item);
    }

    public String generateSelectAllCommand() {
        return "select * from " + tableName;
    }

    public String generateSelectOneByPrimaryKeyCommand(Object key) throws NotImplementedTypesException {
        return "select * from " + tableName + " where " + primaryKeyFieldConverter.getName() + " = "
                + stringValueOf(key);
    }

    protected void writeRow(ResultSet set, T item) throws java.sql.SQLException,
            IllegalAccessException {
        for (FieldConverter field : fields) {
            if (field.getField().getType().equals(Integer.class)) {
                field.getField().set(item, set.getInt(field.getName()));
            }
            if (field.getField().getType().equals(String.class)) {
                field.getField().set(item, set.getString(field.getName()));
            }
        }
    }

    public DataConverter(Class<T> clazz) throws TooManyPrimaryKeys, NotAnnotatedAsTableClassException,
            PrimaryKeyIsNotColumnException, NoPrimaryKeyException {
        if (clazz.getAnnotation(Table.class) == null) {
            throw new NotAnnotatedAsTableClassException();
        } else {
            if (!clazz.getDeclaredAnnotation(Table.class).name().equals("")) {
                tableName = clazz.getDeclaredAnnotation(Table.class).name();
            } else {
                tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
            }
        }
        fields = new ArrayList<FieldConverter>();
        for (Field field : clazz.getDeclaredFields()) {
            Class<?> type = field.getType();
            Annotation primaryKeyAnnotation = field.getDeclaredAnnotation(PrimaryKey.class),
                    columnAnnotation = field.getDeclaredAnnotation(Column.class);

            if (primaryKeyAnnotation != null) {
                if (columnAnnotation == null) {
                    throw new PrimaryKeyIsNotColumnException();
                }
                if (primaryKeyField != null) {
                    throw new TooManyPrimaryKeys();
                }
                primaryKeyField = field;
            }
            if (columnAnnotation != null) {
                String name = null;
                if (!field.getDeclaredAnnotation(Column.class).name().equals("")) {
                    name = field.getDeclaredAnnotation(Column.class).name();
                } else {
                    name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
                }
                FieldConverter currentFieldConverter = new FieldConverter(name, field);
                fields.add(currentFieldConverter);
                if (field.equals(primaryKeyField)) {
                    primaryKeyFieldConverter = currentFieldConverter;
                }

            }
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                annotation.getClass();
            }
        }
        if (primaryKeyField == null) {
            throw new NoPrimaryKeyException();
        }
    }
}
