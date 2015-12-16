package runners;

import jcmdparser.Parser;
import twitter4j.*;
import util.APIException;
import util.QueryBuilder;
import util.StringEditor;

public class StreamRunner {

    static StatusAdapter tweetAdapter = new StatusAdapter() {
        public void onStatus(Status status) {
            System.out.println(StringEditor.tweetStringToPrint(status));
            try {
                Thread.sleep(1000); //1000 milliseconds is one second.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
    };

    public static void runStream(Parser jcp) {
        try {
            TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
            twitterStream.addListener(tweetAdapter);

            FilterQuery filterQuery = QueryBuilder.formFilterQuery(jcp);
            twitterStream.filter(filterQuery);
        } catch (APIException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
