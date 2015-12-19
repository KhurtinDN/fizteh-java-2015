package ru.mipt.diht.students.IrinaMudrova.Twitter;


import ru.mipt.diht.students.IrinaMudrova.Twitter.library.*;
import ru.mipt.diht.students.IrinaMudrova.Twitter.library.exceptions.TwitterParameterException;
import org.junit.*;
import twitter4j.*;

import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class TwitterStreamTest extends TwitterStream {
    private static String[] argQ = {"-q", "Moscow"};
    private static String[] argQS = {"-q", "Moscow", "-s"};
    private static String[] argQPL = {"-q", "Science", "-p", "Moscow", "-l", "100"};
    private static String[] argQPL1 = {"-q", "Science", "-p", "Moscow", "-l", "1"};
    private static String[] argQPHR = {"-q", "Science", "-p", "Moscow", "--hideRetweets"};
    private static String[] argLS = {"-l", "-s"};
    private static String[] argH = {"-h"};

    @Test
    public void testMakeQuery1() throws Exception {
        init();
        TwitterStream ts = new TwitterStream();
        opt = new TwitterOptions();
        opt.parse(argQ);
        err = mock(PrintStream.class);
        YandexPlaces places = mock(YandexPlaces.class);
        factory = mock(TwitterStreamAssistFactory.class);
        twitter4j.Query query = new Query();
        when(factory.newQuery()).thenReturn(query);
        when(factory.newYandexPlaces()).thenReturn(places);
        makeQuery();
        verify(places, never()).setPlaceQuery(anyString());
        assertEquals(query.toString(),
                "Query{query='Moscow', lang='null', locale='null', maxId=-1, count=-1,"
                        + " since='null', sinceId=-1, geocode='null', until='null',"
                        + " resultType='null', nextPageQuery='null'}");
    }

    @Test
    public void testMakeQuery2() throws Exception {
        init();
        TwitterStream ts = new TwitterStream();
        opt = new TwitterOptions();
        opt.parse(argQPL);
        err = mock(PrintStream.class);
        YandexPlaces places = mock(YandexPlaces.class);
        factory = mock(TwitterStreamAssistFactory.class);
        twitter4j.Query query = new Query();
        when(factory.newQuery()).thenReturn(query);
        when(factory.newYandexPlaces()).thenReturn(places);
        when(places.calcCoord()).thenReturn(new double[]{-13, -42});
        when(places.calcRadiusKm()).thenReturn(100.0);
        makeQuery();
        verify(places, times(1)).setPlaceQuery(anyString());
        //System.out.println(query.toString());
        assertEquals(query.toString(),
                "Query{query='Science', lang='null', locale='null', maxId=-1, count=100, "
                        + "since='null', sinceId=-1, geocode='-42.0,-13.0,100.0km',"
                        + " until='null', resultType='null', nextPageQuery='null'}");
    }

    @Test
    public void testShowOneTweet() throws Exception {
        init();
        out = mock(PrintStream.class);
        Status status = mock(Status.class);
        tweetFormatter = mock(TweetFormatter.class);
        when(tweetFormatter.oneTweetToStr(status, TweetFormatter.ShowTime.no)).thenReturn("Hallelujah");
        showOneTweet(status, TweetFormatter.ShowTime.no);
        verify(out, times(1)).println("Hallelujah");
    }

    @Test
    public void testShowTweets1() throws Exception {
        TwitterStream twitterStream = new TwitterStream() {
            @Override
            protected void showOneTweet(Status tweet, TweetFormatter.ShowTime showTime) {
                out.println(tweet.getId());
            }

        };
        twitterStream.init();
        twitterStream.opt = new TwitterOptions();
        twitterStream.opt.parse(argQPL);
        twitterStream.err = mock(PrintStream.class);
        twitterStream.out = mock(PrintStream.class);
        twitterStream.tweetFormatter = mock(TweetFormatter.class);
        Status status1 = mock(Status.class);
        Status status2 = mock(Status.class);
        when(status1.getId()).thenReturn(1L);
        when(status2.getId()).thenReturn(2L);
        List<Status> list = new ArrayList<Status>(2);
        list.add(status1);
        list.add(status2);
        twitterStream.showTweets(list);
        verify(twitterStream.out, times(1)).println(anyString());
        verify(twitterStream.out, times(1)).println(1L);
        verify(twitterStream.out, times(1)).println(2L);
    }

    @Test
    public void testShowTweets2() throws Exception {
        TwitterStream twitterStream = new TwitterStream() {
            @Override
            protected void showOneTweet(Status tweet, TweetFormatter.ShowTime showTime) {
                out.println(tweet.getId());
            }

        };
        twitterStream.init();
        twitterStream.opt = new TwitterOptions();
        twitterStream.opt.parse(argQPHR);
        twitterStream.err = mock(PrintStream.class);
        twitterStream.out = mock(PrintStream.class);
        twitterStream.tweetFormatter = mock(TweetFormatter.class);
        Status status1 = mock(Status.class);
        Status status2 = mock(Status.class);
        when(status1.getId()).thenReturn(1L);
        when(status2.getId()).thenReturn(2L);
        when(status1.isRetweet()).thenReturn(true);
        when(status2.isRetweet()).thenReturn(false);
        List<Status> list = new ArrayList<Status>(2);
        list.add(status1);
        list.add(status2);
        twitterStream.showTweets(list);
        verify(twitterStream.out, times(1)).println(anyString());
        verify(twitterStream.out, never()).println(1L);
        verify(twitterStream.out, times(1)).println(2L);
    }

    @Test
    public void testShowTweets3() throws Exception {
        TwitterStream twitterStream = new TwitterStream() {
            @Override
            protected void showOneTweet(Status tweet, TweetFormatter.ShowTime showTime) {
                out.println(tweet.getId());
            }

        };
        twitterStream.init();
        twitterStream.opt = new TwitterOptions();
        twitterStream.opt.parse(argQPL1);
        twitterStream.err = mock(PrintStream.class);
        twitterStream.out = mock(PrintStream.class);
        twitterStream.tweetFormatter = mock(TweetFormatter.class);
        Status status1 = mock(Status.class);
        Status status2 = mock(Status.class);
        when(status1.getId()).thenReturn(1L);
        when(status2.getId()).thenReturn(2L);
        List<Status> list = new ArrayList<Status>(2);
        list.add(status1);
        list.add(status2);
        twitterStream.showTweets(list);
        verify(twitterStream.out, times(1)).println(anyString());
        verify(twitterStream.out, times(1)).println(1L);
        verify(twitterStream.out, never()).println(2L);
    }

    @Test
    public void testShowTweets4() throws Exception {
        TwitterStream twitterStream = new TwitterStream() {
            @Override
            protected void showOneTweet(Status tweet, TweetFormatter.ShowTime showTime) {
                out.println(tweet.getId());
            }

        };
        twitterStream.init();
        twitterStream.opt = new TwitterOptions();
        twitterStream.opt.parse(argQPHR);
        twitterStream.err = mock(PrintStream.class);
        twitterStream.out = mock(PrintStream.class);
        twitterStream.tweetFormatter = mock(TweetFormatter.class);
        Status status1 = mock(Status.class);
        Status status2 = mock(Status.class);
        when(status1.getId()).thenReturn(1L);
        when(status2.getId()).thenReturn(2L);
        when(status1.isRetweet()).thenReturn(true);
        when(status2.isRetweet()).thenReturn(true);
        List<Status> list = new ArrayList<Status>(2);
        list.add(status1);
        list.add(status2);
        twitterStream.showTweets(list);
        verify(twitterStream.out, never()).println(1L);
        verify(twitterStream.out, never()).println(2L);
        verify(twitterStream.out, times(1)).println("There are not any tweets.");
    }

    @Test
    public void testStartTwitterStream1() throws Exception {
        TwitterStream twitterStream = new TwitterStream() {
            @Override
            protected void startStreaming() {
                fail();
            }
            @Override
            protected void  genAndShowResult() {
                fail();
            }
        };

        twitterStream.init();
        twitterStream.err = mock(PrintStream.class);
        twitterStream.out = mock(PrintStream.class);

        twitterStream.opt = mock(TwitterOptions.class);

        when(twitterStream.opt.parse(argLS)).thenThrow(new TwitterParameterException());
        twitterStream.startTwitterStream(argLS);
        verify(twitterStream.opt).usage(twitterStream.out);

        when(twitterStream.opt.isNeedToShowHelp()).thenReturn(true);
        twitterStream.startTwitterStream(argH);
        verify(twitterStream.opt, times(2)).usage(twitterStream.out);

    }

    @Test
    public void testStartTwitterStream2() throws Exception {
        class TwitterStreamAssistTest extends TwitterStream {
            private int stream;
            @Override
            protected void startStreaming() {
                assertEquals("checking is stream variable default", stream, -1);
                stream = 1;
            }
            @Override
            protected void  genAndShowResult() {
                assertEquals("checking is stream variable default", stream, -1);
                stream = 0;
            }
        }
        TwitterStreamAssistTest twitterStream = new TwitterStreamAssistTest();

        twitterStream.init();
        twitterStream.err = mock(PrintStream.class);
        twitterStream.out = mock(PrintStream.class);

        twitterStream.stream = -1;
        twitterStream.opt = new TwitterOptions();
        twitterStream.startTwitterStream(argQPL);
        assertEquals("checking if there has been launched non-stream mod", twitterStream.stream, 0);

        twitterStream.stream = -1;
        twitterStream.opt = new TwitterOptions();
        twitterStream.startTwitterStream(argQS);
        assertEquals("checking if there has been launched stream mod", twitterStream.stream, 1);
    }



}
