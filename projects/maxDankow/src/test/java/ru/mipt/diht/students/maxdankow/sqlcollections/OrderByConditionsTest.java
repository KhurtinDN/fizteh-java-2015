package ru.mipt.diht.students.maxdankow.sqlcollections;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(DataProviderRunner.class)
public class OrderByConditionsTest {
    @DataProvider
    public static Object[][] orderProvider() {
        return new Object[][]{
                {new Example("first", 2), new Example("second", 1), OrderByConditions.asc(Example::getValue), Integer.compare(2, 1)},
                {new Example("first", 2), new Example("second", 1), OrderByConditions.desc(Example::getValue), Integer.compare(1, 2)}
        };
    }

    @Test
    @UseDataProvider("orderProvider")
    public void checkOrder(Example ex1, Example ex2,
                           Comparator<Example> tested,
                           int expected) {
        assertThat(tested.compare(ex1, ex2), equalTo(expected));
    }
}
