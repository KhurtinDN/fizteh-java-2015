package ru.mipt.diht.students.feezboom.Twitter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

@SuppressWarnings("FieldCanBeLocal")
class JCommanderList {

    private static final int DEFAULT_LIMIT = 100;
    private final JCommander cmd;

    @Parameter(names = { "-q", "--query" },
            description = "Ваши ключевые слова",
            required = true)
    private String query = "";

    @Parameter(names = { "-p", "--place" },
            description = "Поиск по региону ('nearby' - поблизости, пригодно к использованию).")
    private String place = "anywhere";

    @Parameter(names = { "-s", "--stream"},
            description = "Режим стрима.")
    private Boolean stream = false;

    @Parameter(names = { "-l", "--limit"},
            description = "Ограничение на вывод(не пригодно для стрима).")
    private Integer limit = DEFAULT_LIMIT;

    @Parameter(names = "--hideRetweets",
            description = "Не показывать ретвиты.")
    private boolean noRetweets = false;

    @Parameter(names = { "-h", "--help"},
            description = "Режим справки.", help = true)
    private boolean help = false;

    public final String getQuery() {
        return query;
    }

    public final String getPlace() {
        return place;
    }

    public final Integer getLimit() {
        return limit;
    }

    public final boolean isNoRetweets() {
        return noRetweets;
    }

    public final boolean isStream() {
        return stream;
    }

    public final boolean isHelp() {
        return help;
    }

    JCommanderList(String[] args) {
        cmd = new JCommander(this, args);
        cmd.setProgramName("TwitterStreamer");
        cmd.setAcceptUnknownOptions(true);
    }

    public final void printHelp() {
        cmd.usage();
    }
}
