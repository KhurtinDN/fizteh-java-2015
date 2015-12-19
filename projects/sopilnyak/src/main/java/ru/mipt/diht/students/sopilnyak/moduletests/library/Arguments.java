package ru.mipt.diht.students.sopilnyak.moduletests.library;

public class Arguments {

    public static final int START_QUERY = 1;
    public static final int LOCATION = 2;
    public static final int STREAM_MODE = 3;
    public static final int HIDE_RETWEETS = 4;
    public static final int SET_LIMIT = 5;
    public static final int HELP = 6;
    public static final int NEARBY = 7;

    private static String queryString;
    private static boolean isQueryStarted = false;
    private static String locationString;
    private static boolean isLocationStarted = false;
    private static boolean isSetLimitStarted = false;
    private static boolean isNearbyEnabled = false;
    private static boolean isStreamEnabled = false;
    private static int limit = -1;
    private static boolean hideRetweets = false;

    public Arguments() {

    }

    public static boolean parse(String[] args) {

        for (int i = 0; i < args.length; i++) {

            switch (commandNumber(args[i])) {
                case 0: // not a command
                    if (isQueryStarted) {
                        queryString += args[i] + " "; // add to search query
                    }
                    if (isLocationStarted) {
                        locationString += args[i] + " "; // location
                    }
                    if (isSetLimitStarted) {
                        limit = Integer.parseInt(args[i]);
                        isSetLimitStarted = false;
                    }
                    break;

                case START_QUERY: // start new query
                    queryString = "";
                    isQueryStarted = true;
                    isLocationStarted = false;
                    break;

                case LOCATION: // location
                    locationString = "";
                    isLocationStarted = true;
                    isQueryStarted = false;
                    break;

                case STREAM_MODE: // stream mode
                    isStreamEnabled = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                case HIDE_RETWEETS: // hide retweets
                    hideRetweets = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                case SET_LIMIT: // set limit
                    isSetLimitStarted = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                case HELP: // help
                    isQueryStarted = false;
                    isLocationStarted = false;
                    if (!Help.showHelp()) {
                        return false; // problems with help file
                    }
                    break;

                case NEARBY: // nearby
                    isNearbyEnabled = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                default:
                    break;
            }

        }

        if (queryString != null
                && !queryString.equals("")) { // remove space in the end
            queryString = queryString.substring(0,
                    queryString.length() - 1);
        }

        if (locationString != null
                && !locationString.equals("")) { // remove space in the end
            locationString = locationString.substring(0,
                    locationString.length() - 1);
        }

        return !((queryString == null
                || queryString.equals("")) && locationString == null);

    }

    protected static short commandNumber(String arg) {
        if (arg.equals("--query") || arg.equals("-q")) {
            return START_QUERY;
        }
        if (arg.equals("--place") || arg.equals("-p")) {
            return LOCATION;
        }
        if (arg.equals("--stream") || arg.equals("-s")) {
            return STREAM_MODE;
        }
        if (arg.equals("--hideRetweets")) {
            return HIDE_RETWEETS;
        }
        if (arg.equals("--limit") || arg.equals("-l")) {
            return SET_LIMIT;
        }
        if (arg.equals("--help") || arg.equals("-h")) {
            return HELP;
        }
        if (arg.equals("nearby") && isLocationStarted) {
            return NEARBY;
        }
        return 0;
    }

    public static String getQueryString() {
        return queryString;
    }

    public static void setQueryString(String string) {
        queryString = string;
    }

    public static String getLocationString() {
        return locationString;
    }

    public static boolean getIsNearbyEnabled() {
        return isNearbyEnabled;
    }

    public static void setIsNearbyEnabled(boolean enabled) {
        isNearbyEnabled = enabled;
    }

    public static boolean getIsStreamEnabled() {
        return isStreamEnabled;
    }

    public static int getLimit() {
        return limit;
    }

    public static boolean getHideRetweets() {
        return hideRetweets;
    }

}
