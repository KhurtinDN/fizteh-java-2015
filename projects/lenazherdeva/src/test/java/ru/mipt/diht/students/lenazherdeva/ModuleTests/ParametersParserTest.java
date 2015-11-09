/*package ru.mipt.diht.students.lenazherdeva.ModuleTests;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.lenazherdeva.TwitterStream.Parameters;

/**
 * Created by admin on 09.11.2015.

public class ParametersParserTest extends TestCase{
    private JCommander jCommander;
    private Parameters parameters;
    private String [] params;

    @Before
    public void setUp() throws Exception {
        params = new String[9];
        params[0] = "-q";
        params[1] = "me";
        params[3] = "-p";
        params[3] = "Moscow";
        params[4] = "-s";
        params[5] = "--hideRetweets";
        params[6] = "-l";
        params[7] = "50";
        params[8] = "-h";
    }

    @Test
    public void parametersTest() throws Exception {
        parameters = new Parameters();
        jCommander = new JCommander(parameters);
        jCommander.parse(params);
        assertEquals(parameters.getQuery(), "me");
        assertEquals(parameters.getLocation(), "Moscow");
        assertEquals(parameters.isStream(), true);
        assertEquals(parameters.hideRetweets(), true);
        assertEquals(parameters.getLimit(), 50);
        assertEquals(parameters.isHelp(), true);
    }
}*/
