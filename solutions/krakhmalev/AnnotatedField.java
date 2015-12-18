package ru.fizteh.fivt.students.krakhmalev.MiniORM;

import com.google.common.base.CaseFormat;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Annotated;
import ru.fizteh.fivt.students.krakhmalev.MiniORM.Annotations.Column;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class AnnotatedField {
    private String columnName;
    private Field element;

    private static final Map<Class, String> H2MAP;

    public AnnotatedField(Field elem ) {
        element = elem;
        Column column = elem.getAnnotation(Column.class);
        columnName = column.name();
        if (columnName.equals("")) {
            columnName = element.getName();
            columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName);
        }
    }

    public String getColumnName(){
        return columnName;
    }

    public Field getElement() {
        return element;
    }

    public String getH2Type(){
        if (H2MAP.get(element.getType()) == null){
            throw new IllegalArgumentException(columnName + " недопустимый тип для базы данных");
        }
        return H2MAP.get(element.getType());
    }

    static {
        H2MAP = new HashMap<>();
        H2MAP.put(Integer.class, "INT");
        H2MAP.put(Long.class, "INT");
        H2MAP.put(Byte.class, "INT");
        H2MAP.put(Short.class, "INT");
        H2MAP.put(Double.class, "DOUBLE");
        H2MAP.put(Float.class, "DOUBLE");
        H2MAP.put(String.class, "VARCHAR(10)");
        H2MAP.put(Character.class, "VARCHAR(10)");
        H2MAP.put(Integer.TYPE, "INT");
        H2MAP.put(Long.TYPE, "INT");
        H2MAP.put(Byte.TYPE, "INT");
        H2MAP.put(Short.TYPE, "INT");
        H2MAP.put(Double.TYPE, "DOUBLE");
        H2MAP.put(Float.TYPE, "DOUBLE");
        H2MAP.put(Character.TYPE, "VARCHAR(10)");
    }

}
