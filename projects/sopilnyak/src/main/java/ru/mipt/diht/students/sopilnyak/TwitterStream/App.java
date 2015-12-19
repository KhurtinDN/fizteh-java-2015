package ru.mipt.diht.students.sopilnyak.TwitterStream;


import java.io.*;

public class App {

    private static String queryString;
    private static boolean isQueryStarted = false;
    private static String locationString;
    private static boolean isLocationStarted = false;
    private static boolean isSetLimitStarted = false;
    private static boolean isNearbyEnabled = false;
    private static boolean isStreamEnabled = false;
    private static int limit = -1;
    private static boolean hideRetweets = false;

    public static final String HELP_FILE = "help.txt";

    public static final int START_QUERY = 1;
    public static final int LOCATION = 2;
    public static final int STREAM_MODE = 3;
    public static final int HIDE_RETWEETS = 4;
    public static final int SET_LIMIT = 5;
    public static final int HELP = 6;
    public static final int NEARBY = 7;

    public static void main(String[] args) {

        // read commands from console
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
                    showHelp();
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

        if ((queryString == null
                || queryString.equals("")) && locationString == null) {
            System.err.println("No query, nothing to find");
        } else {
            TwitterAPI.addQuery();
        }

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

    protected static void showHelp() {
        File file = new File(HELP_FILE);

        try {

            if (!file.exists()) {
                System.err.println("Нет файла help");
                return;
            }

            BufferedReader in = new BufferedReader(
                    new FileReader(file.getAbsoluteFile()));

            try {
                String string;
                while ((string = in.readLine()) != null) {
                    System.out.println(string);
                }
            } catch (IOException e) {
                System.err.println("Проблема с чтением файла");
            } finally {
                in.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Нет файла help");
        } catch (IOException e) {
            System.err.println("Проблема с чтением файла");
        }
    }

    public static String getQueryString() {
        return queryString;
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
