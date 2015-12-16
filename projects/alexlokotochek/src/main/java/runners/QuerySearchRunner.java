package runners;

import jcmdparser.Parser;
import twitter4j.*;
import util.QueryBuilder;
import util.StringEditor;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class QuerySearchRunner {

    public static void runSearch(Parser jcp) {
        try {
            Query query = QueryBuilder.formQuery(jcp);
            Twitter twitter = TwitterFactory.getSingleton();
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                Date date = status.getCreatedAt();
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(date);
                Calendar now = GregorianCalendar.getInstance();
                now.setTimeInMillis(System.currentTimeMillis());
                System.out.println(StringEditor.tweetStringToPrint(status));
            }
        } catch (Exception te) {
            System.out.print(te.getMessage());
        }
    }
}
