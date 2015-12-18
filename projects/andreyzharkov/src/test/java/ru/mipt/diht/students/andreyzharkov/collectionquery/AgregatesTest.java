package ru.mipt.diht.students.andreyzharkov.collectionquery;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.Aggregates.*;

/**
 * Created by Андрей on 17.12.2015.
 */
public class AgregatesTest {
    private List<Integer> inputData;
    private Function<Integer, Integer> function;

    @Before
    public void setUp() {
        inputData = Arrays.asList(1, 2, -1, 3, -3, -14, 24, 10);
        function = (number) -> number;
    }

    @Test
    public void testMin() throws Exception {
        assertThat(min(function).apply(inputData), is(Collections.min(inputData)));
    }

    @Test
    public void testMax() throws Exception {
        assertThat(max(function).apply(inputData), is(Collections.max(inputData)));
    }

    @Test
    public void testCount() throws Exception {
        assertThat(count(function).apply(inputData), is(Long.valueOf(inputData.size())));
    }

    @Test
    public void testAvg() throws Exception {
        assertThat(avg(function).apply(inputData),
                is((int) inputData.stream().mapToInt(i -> i).average().getAsDouble()));
    }
}
