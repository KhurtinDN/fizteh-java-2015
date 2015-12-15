package ru.mipt.diht.students.maxdankow.miniorm;

import java.lang.reflect.Field;

class ItemColumn {
    ItemColumn(String newName, String newType, Field newField) {
        this.name = newName;
        this.type = newType;
        this.field = newField;
    }

    // Из-за CheckStyle =(
    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        ItemColumn other = (ItemColumn) obj;
        return this.name.equals(other.name)
                && this.type.equals(other.type)
                /*&& this.field == other.field*/;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    private String name;
    private String type;
    private Field field;
}
