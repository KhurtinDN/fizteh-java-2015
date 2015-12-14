package ru.mipt.diht.students.sopilnyak.moduletests;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import ru.mipt.diht.students.sopilnyak.moduletests.library.Results;
import ru.mipt.diht.students.sopilnyak.moduletests.library.UnknownLocationException;
import twitter4j.*;

import java.net.UnknownHostException;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class ResultsTest {


    @Test(expected = UnknownLocationException.class)
    public void testLocation() throws UnknownLocationException {
        try {
            Results.sendQuery("");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
        }
        Results.sendGeoQuery("somerandomlocation12");
        try {
            ArrayList<String> result = Results.printResults();
            for (String tweet : result) {
                System.out.println(tweet);
            }
        } catch (TwitterException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test(expected = TwitterException.class)
    public void testEmptyQuery() throws TwitterException {
        try {
            Results.sendQuery("");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
        }
        try {
            ArrayList<String> result = Results.printResults();
            for (String tweet : result) {
                System.out.println(tweet);
            }
        } catch (UnknownLocationException e) {
            System.err.println(e.getMessage());
        }
    }
}
