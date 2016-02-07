package ru.mipt.diht.students.collectionquerytests;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static ru.mipt.diht.students.collectionquery.Aggregates.*;

/**
 * Created by mikhail on 03.02.16.
 */
public class AggregatesTest {
    @Test
    public void test() {
        assertThat(max(Function.<Integer>identity()).apply(Arrays.asList(2, 1, 7, 3, 15, 6, 2)), is(15));
        assertThat(min(Function.<Integer>identity()).apply(Arrays.asList(2, 1, 7, 3, 15, 6, 2)), is(1));
        assertThat(count((Integer t) -> t == 2 ? null : t).apply(Arrays.asList(2, 1, 7, 3, 15, 6, 2)), is(5L));
        assertThat(avg(Function.<Integer>identity()).apply(Arrays.asList(2, 1, 7, 3, 15, 6, 2)), closeTo(5.14286,
                0.0001));
    }
}