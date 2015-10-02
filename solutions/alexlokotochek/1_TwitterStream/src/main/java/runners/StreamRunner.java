package runners;

import jcmdparser.Parser;
import twitter4j.*;
import util.QueryBuilder;
import util.StringEditor;

public class StreamRunner {

    static StatusListener tweetListener = new StatusListener(){
        public void onStatus(Status status) {
            System.out.println(StringEditor.tweetStringToPrint(status));
            try {
                Thread.sleep(1000); //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
        public void onScrubGeo(long arg0, long arg1) {}
        public void onStallWarning(StallWarning arg0) {}
    };

    public static void runStream (Parser jcp) {

        System.out.print("I'm in runstream!");
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);

        FilterQuery filterQuery = QueryBuilder.formFilterQuery(jcp);
        twitterStream.filter(filterQuery);
    }

}
