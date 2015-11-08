package ru.mipt.diht.students.ale3otik.moduleTests.library;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.Arguments;

/**
 * Created by alex on 08.11.15.
 */
public class ArgumentsParserTest extends TestCase {
    private Arguments arguments;
    private JCommander jcm;
    private String [] args;

    @Before
    public void setUp() throws Exception {
        args = new String[9];
        args[0] = "-q";
        args[1] = "body";
        args[2] = "-l";
        args[3] = "1000";
        args[4] = "-p";
        args[5] = "London";
        args[6] = "--hideRetweets";
        args[7] = "-s";
        args[8] = "-h";
    }

    @Test
    public void testParser() throws Exception{
        arguments = new Arguments();
        jcm = new JCommander(arguments);

        jcm.parse(args);
        assertEquals(arguments.getLimit(), 1000);
        assertEquals(arguments.getQuery(), "body");
        assertEquals(arguments.getLocation(),"London");
        assertEquals(arguments.isHideRetweets(),true);
        assertEquals(arguments.isStream(),true);
        assertEquals(arguments.isHelp(), true);
    }
}
