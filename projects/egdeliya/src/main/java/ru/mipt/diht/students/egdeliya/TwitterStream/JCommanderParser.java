package ru.mipt.diht.students.egdeliya.TwitterStream;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("checkstyle:designforextension")
public class JCommanderParser {
   //список параметров
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"--query", "-q"}, description = "Ключевые слова")
    private String query = "";

    @Parameter(names = {"--place", "-p"}, arity = 1, description = "Твиты по заданному региону")
    //место поиска по умолчанию
    private String place = "";

    @Parameter(names = {"--stream", "-s"}, description = "Печать твитов")
    private boolean stream = false;

    @Parameter(names = "--hideRetweets", description = "Не показывать ретвиты")
    private boolean hideRetweets = false;

    @Parameter(names = {"--limit", "-l"},
            description = "Ограничение числа выведенных твитов (не работает с потоком)")
    private Integer limit = Integer.MAX_VALUE;

    @Parameter(names = {"--help", "-h"}, description = "Справка")
    private boolean help = false;

    public String getLocation() {
        return place;
    }

    public boolean isStream() {
        return stream;
    }

    public boolean isHideRetweets() {
        return hideRetweets;
    }

    public Integer getLimit() {
        return limit;
    }

    public boolean isHelp() {
        return help;
    }

    public String getUsersQuery() {
        return query;
    }
}
