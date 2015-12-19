import java.lang.reflect.Field;

public class TColumn {
    public TColumn(String newName, String newType, Field newField) {
        this.name = newName;
        this.type = newType;
        this.field = newField;
    }

    @Override
    public final int hashCode() {
        return 0;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        TColumn other = (TColumn) obj;
        return this.name.equals(other.name)
                && this.type.equals(other.type);
    }

    public final String getName() {
        return name;
    }

    public final String getType() {
        return type;
    }

    public final Field getField() {
        return field;
    }

    private String name;
    private String type;
    private Field field;
}