package ru.mipt.diht.students.lenazherdeva.moduleTests.twitterStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.lenazherdeva.twitterStream.Parameters;
import ru.mipt.diht.students.lenazherdeva.twitterStream.Search;

import twitter4j.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TwitterStreamAppTest {

    @Mock
    private Twitter twitter;

    @InjectMocks
    private Search search;
    public static List<Status> statuses;

    @Before
    public void setUp() throws Exception {
        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getTweets()).thenReturn(statuses);
        when(queryResult.nextQuery()).thenReturn(null);
        QueryResult emptyQueryResult = mock(QueryResult.class);
        when(emptyQueryResult.getTweets()).thenReturn(Collections.emptyList());
        when(queryResult.nextQuery()).thenReturn(null);
    }

    @Test
    public void simpleSetQueryTest() throws Exception {
        Parameters param = new Parameters();
        param.setQuery("me");
        param.setStream(true);
        param.setLimit(56);
        param.setHideRetwitts(true);
        param.setHelp(false);
        param.setPlace("");
        Query query = search.setQuery(param);
        assertEquals(query.getQuery(), "me");
    }
}
