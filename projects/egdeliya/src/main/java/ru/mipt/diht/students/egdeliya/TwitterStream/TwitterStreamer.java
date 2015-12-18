package ru.mipt.diht.students.egdeliya.TwitterStream;

//import ru.mipt.diht.students.egdeliya.TwitterStream.JCommanderParser;


import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.util.List;

@SuppressWarnings("checkstyle:magicnumber")
public class TwitterStreamer {
    private String usersQuery = "";
    //private boolean stream;
    private boolean hideRetweets = false;
    private boolean isLimit = false;
    private String place;
    private JCommanderParser jCommander = new JCommanderParser();
    private String[] arguments;

    public TwitterStreamer(String[] args) {
        arguments = args;

        //парсим входую строку
        new JCommander(jCommander, arguments);
    }

    public final void help() {
        //пользователь выводит справку
        if (jCommander.isHelp()) {
            JCommander jCommand = new JCommander(jCommander, arguments);
            jCommand.usage();
            return;
        }
    }

    public final void query() throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        Query twitterQuery = new Query(usersQuery);

        //если установлен лимит
        if (isLimit) {
            twitterQuery.setCount(jCommander.getLimit());
        }

        try {
            QueryResult result = twitter.search(twitterQuery);
            for (Status status: result.getTweets()) {

                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());

                //если не нужно прятать ретвиты
                if (!hideRetweets) {
                    List<Status> statusList = twitter.getRetweets(status.getId());
                    for (Status node : statusList) {
                        System.out.println("@" + node.getUser().getScreenName() + " - " + node.getText());
                    }
                }
            }
        } catch (TwitterException t) {
            System.err.println(t.getMessage());
        }
    }

    public final void stream() {
        if (jCommander.isStream()) {
            //игнорируем limit
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }

            }

        }
    }

    public final void limit() {
        if (jCommander.getLimit() < Integer.MAX_VALUE) {
            isLimit = true;
        }
    }

    public final void hideRetweets() {
        if (jCommander.isHideRetweets()) {
            hideRetweets = true;
        }
    }

    public final void twitterStreamRun() {
        //проверяем, что есть параметр help
        help();

        //должны склеить стоки и соединить пробелом
        for (int i = 0; i < jCommander.getQuery().size(); i++) {
            if (i < jCommander.getQuery().size() - 1) {
                usersQuery += jCommander.getQuery().get(i) + " ";
            }
            usersQuery += jCommander.getQuery().get(i);
        }

        //stream();
        limit();
        hideRetweets();

        try {
            query();
        } catch (TwitterException t) {
            System.err.println(t.getMessage());
        }
    }

}
