package miniorm;

import junit.framework.TestCase;
import ru.mipt.diht.students.semyonkozloff.miniorm.annotation.Column;
import ru.mipt.diht.students.semyonkozloff.miniorm.annotation.PrimaryKey;
import ru.mipt.diht.students.semyonkozloff.miniorm.annotation.Table;

@Table
public class TestClass {

    @PrimaryKey
    @Column
    public int key;

    @Column
    public String string1;

    @Column
    public String string2;

    @Column
    public double value;

    public TestClass(int key, String string1, String string2, double value) {
        this.key = key;
        this.string1 = string1;
        this.string2 = string2;
        this.value = value;
    }
}
