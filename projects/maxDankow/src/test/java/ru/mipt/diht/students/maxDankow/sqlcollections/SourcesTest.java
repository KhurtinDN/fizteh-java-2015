package ru.mipt.diht.students.maxDankow.sqlcollections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SourcesTest {
    @Test
    public void listTest() {
        List<String> expected = new ArrayList<>();
        expected.add("first");
        expected.add("second");
        expected.add("third");
        expected.add("first");
        assertThat(Sources.list("first", "second", "third", "first"), equalTo(expected));
    }
}
