package ru.mipt.diht.students.annnvl.TwitterStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TwitterSearchTest {
    @Mock
    private Twitter twitter;

    @InjectMocks
    private TwitterStream search;
    public static List<Status> statuses;

    @Before
    public void setUp() throws Exception {
        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getTweets()).thenReturn(statuses);
        when(queryResult.nextQuery()).thenReturn(null);
        QueryResult emptyQueryResult = mock(QueryResult.class);
        when(emptyQueryResult.getTweets()).thenReturn(new ArrayList());
        when(queryResult.nextQuery()).thenReturn(null);
    }

    @Test
    public void simpleSetQueryTest() throws Exception {
        Parameters param = new Parameters();
        param.setQuery("cook");
        param.setStream(true);
        param.setLimit(40);
        param.setHideRetweets(true);
        param.setHelp(false);
        param.setPlace("Moscow");
        Query query = search.setQuery(param);
        assertEquals(query.getQuery(), "cook");
    }

    @Test
    public void limitSearchResultTest() throws Exception {
        Parameters param = new Parameters();
        param.setQuery("cook");
        param.setStream(true);
        param.setLimit(40);
        param.setHideRetweets(false);
        param.setHelp(false);
        param.setPlace("");
        List<Status> tweets =  search.search(param);
        assertThat(tweets, hasSize(40));
        verify(twitter).search((Query) argThat(hasProperty("query", equalTo("cook"))));
    }

    @Test
    public void emptySearchResultTest() throws Exception {
        Parameters param = new Parameters();
        param.setQuery("cook");
        param.setStream(true);
        param.setLimit(40);
        param.setHideRetweets(false);
        param.setHelp(false);
        param.setPlace("");
        List<Status> tweets =  search.search(param);
        assertThat(tweets, hasSize(0));
    }
}
