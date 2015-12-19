package ru.mipt.diht.students.IrinaMudrova.MiniOrm;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import static junit.framework.Assert.assertTrue;

import static junit.framework.Assert.assertEquals;

import ru.mipt.diht.students.IrinaMudrova.MiniOrm.annotations.*;
import ru.mipt.diht.students.IrinaMudrova.MiniOrm.exceptions.NoPrimaryKeyException;
import ru.mipt.diht.students.IrinaMudrova.MiniOrm.exceptions.NotAnnotatedAsTableClassException;
import ru.mipt.diht.students.IrinaMudrova.MiniOrm.exceptions.PrimaryKeyIsNotColumnException;
import ru.mipt.diht.students.IrinaMudrova.MiniOrm.exceptions.TooManyPrimaryKeys;

public class DataConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testException1() throws Exception {
        thrown.expect(NoPrimaryKeyException.class);
        @Table(name = "cats")
        class Kitten {
            @Column(name = "ID")
            private int id;
            @Column(name = "COLOUR")
            private int colour;
        }
        DataConverter<Kitten> dataConverter = new DataConverter<Kitten>(Kitten.class);
    }

    @Test
    public void testException2() throws Exception {
        thrown.expect(TooManyPrimaryKeys.class);
        @Table(name = "cats")
        class Kitten {
            @PrimaryKey
            @Column
            private int id;
            @PrimaryKey
            @Column(name = "COLOUR")
            private int colour;
        }
        DataConverter<Kitten> dataConverter = new DataConverter<Kitten>(Kitten.class);
    }

    @Test
    public void testException3() throws Exception {
        thrown.expect(NotAnnotatedAsTableClassException.class);
        class Kitten {
            @PrimaryKey
            @Column
            private int id;
            @Column(name = "COLOUR")
            private int colour;
        }
        DataConverter<Kitten> dataConverter = new DataConverter<Kitten>(Kitten.class);
    }

    @Test
    public void testException4() throws Exception {
        thrown.expect(PrimaryKeyIsNotColumnException.class);
        @Table
        class Kitten {
            @PrimaryKey
            public int id;
            @Column(name = "COLOUR")
            public int colour;
        }
        DataConverter<Kitten> dataConverter = new DataConverter<Kitten>(Kitten.class);
    }

    @Test
    public void testLogic() throws Exception {
        @Table
        class Kitten {
            @PrimaryKey
            @Column
            public int id;
            @Column(name = "Ugu")
            public int ug;
        }
        DataConverter<Kitten> dataConverter = new DataConverter<Kitten>(Kitten.class);
        assertEquals("kitten", dataConverter.getTableName());
        assertTrue(dataConverter.getFields().size() == 2);
        assertTrue(dataConverter.getFields().get(0).getField().equals(dataConverter.getPrimaryKeyField()));
        assertEquals("id", dataConverter.getFields().get(0).getName());
        assertEquals("Ugu", dataConverter.getFields().get(1).getName());
        Kitten kitten = new Kitten();
        kitten.ug = 13;
        assertTrue(dataConverter.getFields().get(1).getField().get(kitten).equals(13));
    }


    @Table(name = "dogs")
    public static class Doggy {
        @PrimaryKey
        @Column(name = "ID")
        public Integer id;
        @Column(name = "COLOUR")
        public String colour;
        @Override
        public boolean equals(Object dog) {
            return dog instanceof Doggy && id.equals(((Doggy) dog).id) && colour.equals(((Doggy) dog).colour);
        }
        @Override
        public String toString() {
            return "(" + id + ", " + colour + ")";
        }
        public Doggy() {}
        public Doggy(int id, String colour) {
            this.id = id;
            this.colour = colour;
        }
    }

    @Test
    public void testGenerators() throws Exception {
        DataConverter<Doggy> dataConverter = new DataConverter<Doggy>(Doggy.class);
        Doggy dog = new Doggy(1, "R");
        assertEquals("create table dogs (ID int primary key, COLOUR varchar(255))",
                dataConverter.generateCreateCommand());
        assertEquals("drop table dogs", dataConverter.generateDropCommand());
        assertEquals("insert into dogs values (1, \"R\")", dataConverter.generateInsertCommand(dog));
        assertEquals("update dogs set ID = 1, COLOUR = \"R\" where ID = 1",
                dataConverter.generateUpdateCommand(dog));
        assertEquals("delete from dogs where ID = 1", dataConverter.generateDeleteCommand(dog));
        assertEquals("select * from dogs", dataConverter.generateSelectAllCommand());
        assertEquals("select * from dogs where ID = 1", dataConverter.generateSelectOneByPrimaryKeyCommand(1));
    }

}
