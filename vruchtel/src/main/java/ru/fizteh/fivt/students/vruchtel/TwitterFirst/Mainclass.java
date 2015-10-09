package ru.fizteh.fivt.students.vruchtel.TwitterFirst;
/**
 * Created by �������� on 07.10.2015.
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
        //TwitterFactory().getInstance() ���������� ������ Twitter, � TwitterFactory().GetSingleton()
        //���������� static Twitter

        makeQuery();

        if(twitterStreamArgs.isStreamUse()) {
            workInStreamMode(); //���� ����� ���� --stream
        } else {
            workInCommonMode(); //���� ���� --stream �� �����
        }

        System.exit(0);


        //Status status = twitter.updateStatus("Omnomnomnom");
        //System.out.println("Successfully updated the status to [" + status.getText() + "].");
        //System.exit(0);

    }

    //����� ������� ������, �� �������� ����� ������
    public static void makeQuery() {
        String addToTheQuery = "";
        if(twitterStreamArgs.isHideRetweets()) {
            addToTheQuery = "+exclude:retweets";
        }
        //���������� ����� (����� �� ������ ������)
        System.out.println("The query is: " + twitterStreamArgs.getKeyword() + addToTheQuery);

        //�������������� ������
        String queryKeywords = twitterStreamArgs.getKeyword() + addToTheQuery;
        System.out.println("The queryKeywords is: " + queryKeywords);
        query = new Query(queryKeywords);
        query = new Query(twitterStreamArgs.getKeyword());

        //��� � query ���� ����� setCount - ��� ����� �������� ����������


        //��� ���� ����� ����������� � place, ���� ������ �� ���
        //����� ������������ ���-�� ���� SetGeo
    }

    //���� ����� ���� --stream
    public static void workInStreamMode() {}

    //���� ���� --stream �� �����
    public static void workInCommonMode() throws Exception {
        queryResult = twitter.search(query);
        for(twitter4j.Status status : queryResult.getTweets()) {
           //��� ��������� ������ ������ ����� ����� �������� ��������� �����
           System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
       }
    }

    private static Twitter twitter;
    private static TwitterStreamArgs twitterStreamArgs;
    private static Query query;
    private static QueryResult queryResult;
}
