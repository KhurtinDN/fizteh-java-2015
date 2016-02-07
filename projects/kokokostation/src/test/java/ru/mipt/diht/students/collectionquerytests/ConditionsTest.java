package ru.mipt.diht.students.collectionquerytests;

import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static ru.mipt.diht.students.collectionquery.Conditions.like;

/**
 * Created by mikhail on 03.02.16.
 */
public class ConditionsTest {
    @Test
    public void test() {
        Predicate<String> testPredicate = like(Function.identity(), "my_te.*st%");

        assertThat(testPredicate.test("my te.*st)))))"), is(true));
        assertThat(testPredicate.test("my teist"), is(false));
        assertThat(testPredicate.test("my teiiist"), is(false));
        assertThat(testPredicate.test("my teiistii"), is(false));
        assertThat(testPredicate.test("myte.*st"), is(false));
        assertThat(testPredicate.test("my  te.*st"), is(false));
    }
}