package ru.mipt.diht.students.tveritinova.TwitterStream;

import com.beust.jcommander.Parameter;

public class MyJCommander {

    @Parameter(names = {"--stream", "-s"}, description
            = "равномерно и непрерывно с задержкой в 1 секунду "
            + "печать твиты на экран")
    private boolean isStream = false;

    public final boolean getIsHelp() {
        return help;
    }

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help = false;

    @Parameter(names = {"--hideRetweets"}, description = "фильтровать ретвиты")
    private boolean isHideRetweets = false;

    @Parameter(names = {"--limit", "-l"}, description
            = "выводить столько твитов. Не применимо для --stream режима")
    private int limit = -1;

    @Parameter(names = {"--query", "-q"}, description
            = "параметр поиска твитов")
    private String query = null;

    @Parameter(names = {"--place", "-p"}, description
            = "искать по заданному региону (например, "
            + "\"долгопрудный\", \"москва\", \"татарстан\")")
    private String location = null;

    public final boolean getIsStream() {
        return isStream;
    }

    public final boolean getIsHideRetweets() {
        return isHideRetweets;
    }

    public final int getLimit() {
        return limit;
    }

    public final String getQuery() {
        return query;
    }

    public final String getLocation() {
        return location;
    }
}
