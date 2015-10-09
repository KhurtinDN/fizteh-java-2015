package ru.fizteh.fivt.students.vruchtel.TwitterFirst;
/**
 * Created by —ерафима on 07.10.2015.
 */
import twitter4j.Status;
//import twitter4j.Twitter;
//import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import jdk.net.SocketFlow;
import twitter4j.*;
import com.beust.jcommander.*;

public class Mainclass {

    public static void main(String args[]) throws Exception {
        twitterStreamArgs = new TwitterStreamArgs(args);

        twitter = new TwitterFactory().getSingleton();
        //TwitterFactory().getInstance() возвращает просто Twitter, а TwitterFactory().GetSingleton()
        //возвращает static Twitter

        makeQuery();

        //todo вот эту часть кода, очевидно, надо будет отсюда куда-то убрать
      /*  twitter.setOAuthConsumer("IwWQQhD7Ksmflz0DSHDH0lARy", "Jj9Uvz7fpS98ndkBFcqtFlrOVZd61Tu43Fgy54fsnzSoxDjHng");
        AccessToken accessToken = new AccessToken("3892021875-3VGuiQGdrgUMLMCG4IAlLncb0oOWKBRiDFDOx6C",
                "CfZ7Tz2JXeuckQH9aXulbM4Ew6kUb2pqPxnbWZDGPic0E");
        twitter.setOAuthAccessToken(accessToken);*/

        if(twitterStreamArgs.isStreamUse()) {
            workInStreamMode(); //если задан флаг --stream
        } else {
            workInCommonMode(); //если флаг --stream не задан
        }

        System.exit(0);


        //Status status = twitter.updateStatus("Omnomnomnom");
        //System.out.println("Successfully updated the status to [" + status.getText() + "].");
        //System.exit(0);

    }

    //нужно сделать запрос, по которому будем искать
    public static void makeQuery() {
        String addToTheQuery = "";
        if(twitterStreamArgs.isHideRetweets()) {
            addToTheQuery = "+exclude:retweets";
        }
        //отладочный вывод (потом не забыть убрать)
        System.out.println("The query is: " + twitterStreamArgs.getKeyword() + addToTheQuery);

        //инициализируем запрос
        String queryKeywords = twitterStreamArgs.getKeyword() + addToTheQuery;
        System.out.println("The queryKeywords is: " + queryKeywords);
        query = new Query(queryKeywords);
        query = new Query(twitterStreamArgs.getKeyword());

        //ещЄ у query есть метод setCount - это чтобы задавать количество


        //тут надо будет разобратьс€ с place, пока забьЄм на это
        //можно использовать что-то типа SetGeo
    }

    //если задан флаг --stream
    public static void workInStreamMode() {}

    //если флаг --stream не задан
    public static void workInCommonMode() throws Exception {
        queryResult = twitter.search(query);
        for(twitter4j.Status status : queryResult.getTweets()) {
           //дл€ красивого вывода вообще нужно будет написать отдельный класс
           System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
       }
    }

    private static Twitter twitter;
    private static TwitterStreamArgs twitterStreamArgs;
    private static Query query;
    private static QueryResult queryResult;
}
