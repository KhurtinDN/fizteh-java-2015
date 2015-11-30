package ru.mipt.diht.students.lenazherdeva.twitterStream; /**
 * Created by admin on 27.09.2015.
 */
import com.beust.jcommander.JCommander;
import twitter4j.*;

public class TwitterStreamApp {

    public static void main(String[] args) throws Exception {
        Parameters param = new Parameters();
        JCommander cmd = null;
        try {
            cmd = new JCommander(param, args);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        if (param.isHelp()) {
            cmd.usage();
            System.exit(0);
        }
        if (param.isStream()) {
            try {
                TwitterStream twitterStream;
                twitterStream = new TwitterStreamFactory().getInstance();
                StreamMode stream = new StreamMode(twitterStream);
                stream.streamPrint(param);
            } catch (TwitterException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        } else {
            try {
                Twitter twitter = new TwitterFactory().getInstance();
                Search search = new Search(twitter);
                search.searchResult(param).stream().forEach(System.out::println);
            } catch (NoTweetsException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}


