package ru.mipt.diht.students.maxDankow.sqlcollections;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.mipt.diht.students.maxDankow.sqlcollections.aggregator.Aggregator;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(DataProviderRunner.class)
public class AgregatorsTest {
    @DataProvider
    public static Object[][] aggregatesProvider() {
        return new Object[][]{
                {examples, Aggregates.max(Example::getValue), 100},
                {examples, Aggregates.max(Example::getName), "third"},
                {examples, Aggregates.min(Example::getValue), -100},
                {examples, Aggregates.avg(Example::getValue), -0.5},
                {examples, Aggregates.count(Example::getName), 3},
                {examples, Aggregates.count(Example::getValue), 4}
        };
    }

    private static List<Example> examples;

    @BeforeClass
    public static void setUp() {
        examples = new ArrayList<>();
        examples.add(new Example("first", 100));
        examples.add(new Example("second", 1));
        examples.add(new Example("third", -3));
        examples.add(new Example("first", -100));
    }

    @Test
    @UseDataProvider("aggregatesProvider")
    public void applyAgregatorTest(List<Example> list, Aggregator aggregator, Object expected) {
        assertThat(aggregator.apply(list), equalTo(expected));
    }
}
