package ru.mipt.diht.students.annnvl.TwitterStream;

import com.beust.jcommander.Parameter;

class Parameters {

    public static final int HUNDRED = 100;
    @Parameter(names = {"-q", "--query"},
            description = "Запрос или ключевые слова")
    private String query = "";
    @Parameter(names = {"-p", "--place"},
            description = "Место поиска")
    private String place = "";
    @Parameter(names = {"-s", "--stream"},
            description = "Стрим")
    private boolean stream = false;
    @Parameter(names = {"--hideRetwitts"},
            description = "Прятать ретвиты")
    private boolean hideRetweets = false;
    @Parameter(names = {"-h", "--help"},
            description = "Выводит подсказку")
    private boolean help = false;
    @Parameter(names = {"-l", "--limit"},
            description = "Ограничение на количество" + " выводимых твитов (не в стриме)")
    private Integer limit = HUNDRED;

    public final String getQuery() {
        return query;
    }

    public final String getPlace() {
        return place;
    }

    public final Integer getLimit() {
        return limit;
    }

    public final boolean isStream() {
        return stream;
    }

    public final boolean isHideRetweets() {
        return hideRetweets;
    }

    public final boolean isHelp() {
        return help;
    }

    public final void setQuery(String query) { query = query; }

    public final void setLimit(Integer limit) { limit = limit; }

    public final void setStream(boolean stream) { stream = stream; }

    public final void setHideRetweets(boolean hideRetweets) { hideRetweets = hideRetweets; }

    public final void setHelp(boolean help) { help = help; }

    public final void setPlace(String place) { place = place; }
};
