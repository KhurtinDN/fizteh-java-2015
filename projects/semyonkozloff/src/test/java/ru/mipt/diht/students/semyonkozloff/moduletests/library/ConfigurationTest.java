package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.beust.jcommander.JCommander;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(DataProviderRunner.class)
public class ConfigurationTest extends TestCase {

    Configuration configuration = new Configuration();
    JCommander jCommander = new JCommander(configuration);

    @DataProvider
    public static Object[][] argumentStringsDataProvider() {
        return new Object[][] {
                {
                        "--help",
                        null,
                        null,
                        false,
                        false,
                        16,
                        true
                },
                {
                        "-q \"python\" -s -r",
                        "python",
                        null,
                        true,
                        true,
                        16,
                        false
                },
                {
                        "--query \"java\"",
                        "java",
                        null,
                        false,
                        false,
                        16,
                        false
                },
                {
                        "-l 100 -q \"c++\" -r -p \"Moscow\"",
                        "c++",
                        "Moscow",
                        false,
                        true,
                        100,
                        false
                }
        };
    }

    @Test
    @UseDataProvider("argumentStringsDataProvider")
    public void testGetters(String argumentString,
                            String expectedQuery,
                            String expectedLocation,
                            boolean expectedStreamFlag,
                            boolean expectedRetweetsHidingFlag,
                            int expectedLimit,
                            boolean expectedHelpFlag) {
        jCommander.parse(argumentString.split(" "));

        assertThat(configuration.getQuery(), equalTo(expectedQuery));
        assertThat(configuration.getLocation(), equalTo(expectedLocation));
        assertThat(configuration.isStream(), equalTo(expectedStreamFlag));
        assertThat(configuration.shouldHideRetweets(),
                equalTo(expectedRetweetsHidingFlag));
        assertThat(configuration.getLimit(), equalTo(expectedLimit));
        assertThat(configuration.isHelp(), equalTo(expectedHelpFlag));
    }
}
