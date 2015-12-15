package ru.mipt.diht.students.maxdankow.miniorm;

import java.lang.reflect.Field;

class ItemColumn {
    public ItemColumn(String name, String type, Field field) {
        this.name = name;
        this.type = type;
        this.field = field;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        ItemColumn other = (ItemColumn) obj;
        return this.name.equals(other.name)
                && this.type.equals(other.type)
                /*&& this.field == other.field*/;
    }

    public String name;
    public String type;
    Field field;
}
