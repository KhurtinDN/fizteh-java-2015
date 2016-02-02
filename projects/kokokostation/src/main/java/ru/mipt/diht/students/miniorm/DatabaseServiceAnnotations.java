package ru.mipt.diht.students.miniorm;

import javafx.util.Pair;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by mikhail on 29.01.16.
 */
public class DatabaseServiceAnnotations {
    @Retention (RetentionPolicy.RUNTIME)
    @Target (ElementType.TYPE)
    public @interface Table {
        String name() default NO_NAME;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String name() default NO_NAME;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PrimaryKey {}

    private static final String NO_NAME = "";

    private final Class<?> type;

    DatabaseServiceAnnotations(Class type) {
        this.type = type;
    }

    String getTableName() throws DatabaseServiceException {
        if(!type.isAnnotationPresent(Table.class))
            throw new DatabaseServiceException("No @Table annotation: " + type.getName());

        Table typeAnnotation = (Table) type.getAnnotation(Table.class);
        return getName(typeAnnotation.name(), type.getName());
    }

    String getFieldName(Field field) throws DatabaseServiceException {
        if(!field.isAnnotationPresent(Column.class))
            throw new DatabaseServiceException("No @Field annotation: " + type.getName() + "." + field.getName());

        Column fieldAnnotation = (Column) field.getAnnotation(Column.class);
        return getName(fieldAnnotation.name(), field.getName());
    }

    private String getName(String annotationName, String name) {
        if(Objects.equals(annotationName, NO_NAME)) {
            return StringProcessor.fromCamelCaseToLowerUnderscore(name);
        } else {
            return annotationName;
        }
    }

    Pair<List<ru.mipt.diht.students.miniorm.Column>, ru.mipt.diht.students.miniorm.Column> parseType()
            throws DatabaseServiceException {
        ru.mipt.diht.students.miniorm.Column primaryKey = null;
        LinkedList<ru.mipt.diht.students.miniorm.Column> columns = new LinkedList<>();

        for (Field field : type.getFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                columns.add(new ru.mipt.diht.students.miniorm.Column(getFieldName(field), field));

                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    if (primaryKey != null) {
                        throw new DatabaseServiceException("Multiple PrimaryKey: " + type.getName());
                    }

                    primaryKey = columns.getLast();
                }
            }
        }

        if (primaryKey == null) {
            throw new DatabaseServiceException("PrimaryKey isn't set: " + type.getName());
        }

        return new Pair<>(columns, primaryKey);
    }
}
