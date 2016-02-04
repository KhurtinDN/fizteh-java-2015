package ru.mipt.diht.students.twitterstreamtests;

import org.junit.Test;
import ru.mipt.diht.students.twitterstream.PropertiesHelper;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 04.02.16.
 */
public class PropertiesHelperTest {
    @Test (expected = IOException.class)
    public void test() throws IOException {
        PropertiesHelper.getProperty("Incorrect filename", "Nonexistent field");
    }
}