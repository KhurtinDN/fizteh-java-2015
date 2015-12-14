package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

public class HelpCommand extends Commands {
    static final String LIGHT_BLUE = "\033[36m";
    static final String PURPLE = "\033[35m";
    static final String BLACK = "\033[0m";

    @Override
    public void execute(TwitterProvider twitter) {

        System.out.println(PURPLE + "Параметры\n"
                + LIGHT_BLUE + "stream " + BLACK
                + "- равномерно и непрерывно с задержкой в 1 секунду печатает твиты на экран. [--stream|-s]\n"
                + LIGHT_BLUE + "limit " + BLACK
                + "- n твитов. [--limit|-l <tweets>]\n"
                + LIGHT_BLUE + "query " + BLACK
                + " - поиск по заданному запросу. [--query|-q <query or keywords for stream>)]\n"
                +  LIGHT_BLUE + "place " + BLACK
                + "- поиск по заданному региону. [--place|-p <location|'nearby'>]\n"
                + LIGHT_BLUE + "allPlace " + BLACK
                + "- отменить place режим. [--place|-p 0]\n"
                + LIGHT_BLUE + "hideRetweets " + BLACK
                + "- филтр ретвитов. [--hideRetweets|-hRtws +]\n"
                + LIGHT_BLUE + "Retweets " + BLACK
                + "- отменить hideRetweets режим. [--hideRetweets|-hRtws -]\n"
                + LIGHT_BLUE + "help " + BLACK
                + "- печатает справку. [--help|-h]\n"
                + LIGHT_BLUE + "exit " + BLACK
                + "- выход. [--exit|-e]\n");

    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
