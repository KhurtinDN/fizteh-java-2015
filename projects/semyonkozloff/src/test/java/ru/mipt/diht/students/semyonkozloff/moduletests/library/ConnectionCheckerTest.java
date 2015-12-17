package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static ru.mipt.diht.students.semyonkozloff
        .moduletests.library.ConnectionChecker.*;

@RunWith(DataProviderRunner.class)
public class ConnectionCheckerTest extends TestCase {

    @DataProvider
    public static Object[][] existentDomainsDataProvider() {
        return new Object[][] {
                { "google.com" },
                { "yandex.ru" },
                { "vk.com" },
                { "api.twitter.com" }
        };
    }

    @DataProvider
    public static Object[][] nonexistentDomainsDataProvider() {
        return new Object[][] {
                { "tratatatata.ru" },
                { "lalalalala.org" },
        };
    }

    @Test
    @UseDataProvider("existentDomainsDataProvider")
    public void testConnectionToExistentDomains(String existentDomain)
            throws IOException, InterruptedException {
        assertTrue(isInternetReachable(existentDomain));
        assertTrue(hasConnection(existentDomain));
    }

    @Test
    @UseDataProvider("nonexistentDomainsDataProvider")
    public void testConnectionToNonexistentDomains(String nonexistentDomain)
            throws IOException, InterruptedException {
        assertFalse(isInternetReachable(nonexistentDomain));
        assertFalse(hasConnection(nonexistentDomain));
    }
}
