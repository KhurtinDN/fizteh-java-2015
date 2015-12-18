package ru.fizteh.fivt.students.popova.TwitterFirst;

import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class StreamReaderService {
    public static void readTwitterFeed(String keyword){

        TwitterStream stream = TwitterStreamJavaUtil.getStream();

        StatusListener listener = new StatusAdapter() {

            @Override
            public void onException(Exception e) {
                System.out.println("Exception occured:" + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onStatus(Status status) {
                try{
                    PrintTweet.print(status, false, System.out);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        };

        FilterQuery qry = new FilterQuery();

        qry.track(keyword);

        stream.addListener(listener);
        stream.filter(qry);
    }

    public static void readLimitedTwitterFeed(int NumberOfTweets, String keywords )throws TwitterException{
       // TwitterStream LimitedStream = TwitterStreamJavaUtil.getStream();
        Twitter twitter = TwitterJavaUtil.getTweets();
        int numberPrintedTweets = 0;
       // boolean limitFlag = false;
        Query query = new Query(keywords);
        QueryResult qr = twitter.search(query);
        List<Status> qrTweets = qr.getTweets();
        for(Status s: qrTweets){
            PrintTweet.print(s, true, System.out);
            ++numberPrintedTweets;
            if(numberPrintedTweets == NumberOfTweets){
                break;
            }
        }
    }
    public static void main(String[] args) throws Exception {
        System.out.println("This is main !");
        CommandParser parser = new CommandParser();
        JCommander jc = new JCommander(parser, args);
        if (parser.giveHelp()) {
            printHelp();
        }
        if(parser.IsStream()){
            readTwitterFeed(parser.QueryWord());
        }
        if(parser.IsStream()&&parser.IsLimited()){
             System.out.println("incompatible modes");
        }
        else{
            readLimitedTwitterFeed(parser.Limit(), parser.QueryWord());
        }
    }

    private static void printHelp() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("Help.txt"), Charset.defaultCharset());
        for(String x: lines){
            System.out.println(x+"\n");
        }
    }

}
