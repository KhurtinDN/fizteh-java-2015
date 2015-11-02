package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 23.09.15.
 */

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;

/**
 * Input parameters format.
 *
 * [--query|-q <query or keywords for stream>]
 * [--place|-p <location|'nearby'>]
 * [--stream|-s]
 * [--hideRetweets]
 * [--limit|-l <tweets>]
 * [--help|-h]
 */
class Arguments {
    private static final int STANDART_LIMIT = 30;

    private GeoLocationInfo geoLocationInfo = null;
    private String curLocationName = "";

    @Parameter(names = {"--query", "-q"},
            description = "set query parameters")
    private String query = "";

    @Parameter(names = {"--place", "-p"},
            description = "set location")
    private String location = "";

    @Parameter(names = {"--stream", "-s"},
            description = "set type of action: stream ")
    private boolean stream = false;

    @Parameter(names = {"--hideRetweets"},
            description = "tweets only")
    private boolean hideRetweets = false;

    @Parameter(names = {"--limit", "-l"},
            validateWith = PositiveInteger.class,
            description = "set limit on quantity of tweets")
    private int limit = STANDART_LIMIT;

    @Parameter(names = {"--help", "-h"},
            description = "print help manual")
    private boolean help = false;

    public String getQuery() {
        return query;
    }

    public String getLocation() {
        return location;
    }

    public boolean isStream() {
        return stream;
    }

    public boolean isHideRetweets() {
        return hideRetweets;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isHelp() {
        return help;
    }

    public void setGeoLocationInfo(GeoLocationInfo newGeoLocationInfo) {
        this.geoLocationInfo = newGeoLocationInfo;
    }

    public GeoLocationInfo getGeoLocationInfo() {
        return this.geoLocationInfo;
    }

    public String getCurLocationName() {
        return this.curLocationName;
    }

    public void setCurLocationName(String locationName) {
        this.curLocationName = locationName;
    }
}

