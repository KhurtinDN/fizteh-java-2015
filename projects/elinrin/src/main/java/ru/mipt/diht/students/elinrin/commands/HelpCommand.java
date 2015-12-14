package ru.mipt.diht.students.elinrin.commands;

import ru.mipt.diht.students.elinrin.TwitterProvider;

public class HelpCommand extends Commands {
    @Override
    public void execute(TwitterProvider twitter) {
        System.out.println( "\033[1;35m" + "Параметры\n" +
                "\033[36m" + "stream " + "\033[0m" +
                "- равномерно и непрерывно с задержкой в 1 секунду печатает твиты на экран. [--stream|-s]\n" +

                "\033[36m" + "limit " + "\033[0m" +
                "- n твитов. [--limit|-l <tweets>]\n" +

                "\033[36m" + "query " + "\033[0m" +
                " - поиск по заданному запросу. [--query|-q <query or keywords for stream>)]\n" +

                "\033[36m" + "place " + "\033[0m" +
                "- поиск по заданному региону. [--place|-p <location|'nearby'>]\n" +
                "\033[36m" + "allPlace " + "\033[0m" +
                "- отменить place режим. [--place|-p 0]\n" +

                "\033[36m" + "hideRetweets " + "\033[0m" +
                "- филтр ретвитов. [--hideRetweets|-hRtws +]\n" +
                "\033[36m" + "Retweets " + "\033[0m" +
                "- отменить hideRetweets режим. [--hideRetweets|-hRtws -]\n" +

                "\033[36m" + "help " + "\033[0m" +
                "- печатает справку. [--help|-h]\n" +

                "\033[36m" + "exit " + "\033[0m" +
                "- выход. [--exit|-e]\n");

    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
