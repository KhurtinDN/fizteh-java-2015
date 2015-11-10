package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import junit.framework.TestCase;

import org.mockito.Mockito;

import ru.mipt.diht.students.pitovsky.twitterstream.ConsoleUtils;
import ru.mipt.diht.students.pitovsky.twitterstream.ConsoleUtils.TextColor;
import ru.mipt.diht.students.pitovsky.twitterstream.TwitterClient;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterClientTest extends TestCase {
    
    private static Date localToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneOffset.systemDefault()).toInstant());
    }
    
    @Test
    public void testConvertDate() {
        Clock clock = Clock.fixed(LocalDateTime.of(2010, 1, 1, 15, 20).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC);
        TwitterClient twitterClient = new TwitterClient(true, true, clock);
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        LocalDateTime testDateTime = currentDateTime.minusMinutes(1);
        
        twitterClient = new TwitterClient(true, true, clock);
        testDateTime = currentDateTime.minusMinutes(45);
        assertEquals("45 минут назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusMinutes(75);
        assertEquals("1 час назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusHours(13);
        assertEquals("13 часов назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusHours(18);
        assertEquals("вчера", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusDays(1);
        assertEquals("вчера", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusDays(1).minusHours(17);
        assertEquals("1 день назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusDays(44);
        assertEquals("44 дня назад", twitterClient.convertDate(localToDate(testDateTime)));
    }
    
    @Test
    public void testPrintTweets() throws TwitterException {
        LocalDateTime testDateTime = LocalDateTime.of(2011, 1, 1, 15, 20);
        Clock clock = Clock.fixed(testDateTime.toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC);
        
        List<Status> tweetList = new ArrayList<Status>();
        List<String> tweetResultList = new ArrayList<String>();
        for (int i = 0; true; ++i) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("/tweet" + i + ".json");
                if (inputStream == null) {
                    break;
                }
                StringBuilder source = new StringBuilder("");
                char ch = (char) inputStream.read();
                while (ch != (char) (-1)) {
                    source.append(ch);
                    ch = (char) inputStream.read();
                }
                JSONTweet tweetJSON = new JSONTweet(new JSONObject(source.toString()));
                Status tweet = tweetJSON.getMockedTweet();
                Mockito.when(tweet.getCreatedAt()).thenReturn(localToDate(testDateTime.minusSeconds(5)));
                tweetList.add(tweet);
                tweetResultList.add(tweetJSON.result);
            } catch (IOException ioe) {
                System.err.println("io " + ioe.getMessage());
                break;
            } catch (JSONException e) {
                System.err.println("json " + e.getMessage());
            }
        }
        //System.err.println("Successfully parsed " + tweetList.size() + " tweets.");
        
        QueryResult result = Mockito.mock(QueryResult.class);
        Mockito.when(result.nextQuery()).thenReturn(null);
        Mockito.when(result.getTweets()).thenReturn(tweetList);
        
        Twitter twitter = Mockito.mock(Twitter.class);
        Mockito.when(twitter.search(Mockito.any())).thenReturn(result);

        PrintStream standartOut = System.out;
        OutputStream clientByteOut = new ByteArrayOutputStream();
        PrintStream clientOut = new PrintStream(clientByteOut, true);
        System.setOut(clientOut);
        
        TwitterClient twitterClient = new TwitterClient(false, true, clock, twitter);
        twitterClient.printTweets("none", null, 100);
        
        System.setOut(standartOut);

        //System.out.println("yea:" + clientByteOut.toString());
        String[] tweetsOut = clientByteOut.toString().split("[\n]");
        for (int i = 0; i < tweetList.size(); ++i) {
            assertEquals(ConsoleUtils.colorizeString("[только что]", TextColor.GREEN)
                    + tweetResultList.get(i), tweetsOut[i]);
        }
    }
}
