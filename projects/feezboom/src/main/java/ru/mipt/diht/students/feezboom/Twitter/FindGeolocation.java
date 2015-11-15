package ru.mipt.diht.students.feezboom.Twitter;

import ru.mipt.diht.students.feezboom.StringUtils.StringUtils;
import twitter4j.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Vector;

class FindGeolocation {

    private static String getExternalIP() throws Exception {
        URL url = new URL("http://myexternalip.com/raw");
        URLConnection urlConnection = url.openConnection();
        Scanner in = new Scanner(urlConnection.getInputStream());
        String ip = in.nextLine();
        in.close();
        return ip;
    }

    public static String getPlaceStringAlternative() throws Exception {
        File file = new File("api.db-ip.com.txt");
        FileReader reader = new FileReader(file);
        Scanner in = new Scanner(reader);

        String  apiKey = in.nextLine(),
                ip = getExternalIP();
        in.close();

        URL url = new URL("http://api.db-ip.com/addrinfo?addr=" + ip
                + "&api_key=" + apiKey);
        URLConnection urlCon = url.openConnection();

        in = new Scanner(urlCon.getInputStream());
        //We will get string like
        //{"address":"93.175.2.215",
        // "country":"RU",
        // "stateprov":"Moskovskaya oblast",
        // "city":"Dolgoprudny"}
        String info = in.nextLine();
        in.close();
        int first = info.indexOf("city") + "city\":\"".length();
        int last = info.lastIndexOf("\"}");
        return StringUtils.translitToRussian(info.substring(first, last));
    }

    public static String getPlaceString() throws IOException {
        URL url = new URL("http://ip2geolocation.com/");
        URLConnection urlConnection = url.openConnection();
        Scanner scanner =
                new Scanner(urlConnection.getInputStream(), "MacCyrillic");

        //I must find it from a source code (country)
        final int neededLine = 6;
        String substring = "";

        for (int i = 0; i < neededLine; i++) {
            substring = scanner.nextLine();
        }

        //Finding country from source code
        int first = substring.indexOf("—трана: ") + "—трана: ".length();
        int last = substring.indexOf(", –егион");
        String country = substring.substring(first, last);

        //Finding city from source code
        first = substring.indexOf("√ород: ") + "√ород: ".length();
        last = substring.indexOf("\">");
        String city = substring.substring(first, last);

        //If country == Russia -> city's name has russian equivalent
        if (country.equals("Russian Federation")) {
            city = StringUtils.translitToRussian(city);
        }
        return city;
    }

    public static GeoLocation getGeoLocation() throws Exception {
        URL url = new URL("http://ip2geolocation.com/");
        URLConnection urlConnection = url.openConnection();
        Scanner scanner = new Scanner(urlConnection.getInputStream(), "UTF-8");

        final int latitudeLine = 132;
        //longitude line is 133, there is no need to make variable

        for (int i = 1; i < latitudeLine; i++) {
            scanner.nextLine();
        }

        String latStr = scanner.nextLine();
        String lonStr = scanner.nextLine();

        scanner.close();

        double latitude, longitude;

        int first = latStr.lastIndexOf("\">") + "\">".length();
        int last  = latStr.lastIndexOf("</td>");

        latitude = Double.parseDouble(latStr.substring(first, last));

        first = lonStr.lastIndexOf("\">") + "\">".length();
        last  = lonStr.lastIndexOf("</td>");

        longitude = Double.parseDouble(lonStr.substring(first, last));

        //returning geolocation by received latitude and longitude by ip
        return new GeoLocation(latitude, longitude);
    }

    public static Query setSearchPlace(Twitter twitter, Query query,
                                 String placeString) throws Exception {
        //Search by places
        Vector<GeoLocation> locations = new Vector<>();
        GeoQuery geoQuery = new GeoQuery("0.0.0.0");
        geoQuery.setQuery(placeString);
        //Then getting list of places:
        ResponseList<Place> responseList;
        responseList = twitter.searchPlaces(geoQuery);
        //Then looking through responseList,
        // we will find the center of coordinates
        for (Place place : responseList) {

            for (int i = 0; i < place.getBoundingBoxCoordinates().length; i++) {
                for (int j = 0;
                     j < place.getBoundingBoxCoordinates()[i].length; j++) {
                    locations.add(place.getBoundingBoxCoordinates()[i][j]);
                }
            }
        }

        //Then getting center
        double[] center = GeoUtils.calculateCenter(locations);
        double x = center[0];
        double y = center[1];
        //Center is OK

        //Then getting Radius
        double radius = GeoUtils.calculateRadius(locations, x, y);
        System.out.println("Место = " + placeString + " Радиус поиска = " + radius);
        //Radius is OK

        //Then making geolocation for query
        GeoLocation ourLocation = new GeoLocation(x, y);
        query.setGeoCode(ourLocation, radius, Query.Unit.km);
        //OK

        return query;
    }

}

class GeoUtils {
    private static final double DEG_TO_KM = 60 * 1.1515 * 1.609344;
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180 / Math.PI;

    public static double calculateRadius(Vector<GeoLocation> locations, double x, double y) {
        double radius = 0;
        final double minRadius = 10;
        for (GeoLocation geoLocation : locations) {
            radius += getDistanceBetweenCoordinates(x, y,
                    geoLocation.getLatitude(),
                    geoLocation.getLongitude());
        }
        radius /= locations.size();
        if (radius < minRadius) {
            radius = minRadius;
        }
        return radius;
    }

    public static double[] calculateCenter(Vector<GeoLocation> locations) {
        double[] center = new double[2];
        double x = 0, y = 0;
        for (GeoLocation geoLocation : locations) {
            x += geoLocation.getLatitude();
            y += geoLocation.getLongitude();
        }
        x /= locations.size();
        y /= locations.size();

        center[0] = x;
        center[1] = y;

        return center;
    }

    public static double getDistanceBetweenCoordinates(double latitude1,
                                                       double longitude1,
                                                       double latitude2,
                                                       double longitude2) {

        double theta = longitude1 - longitude2;
        double dist = Math.sin(latitude1 * DEG_TO_RAD)
                * Math.sin(latitude2 * DEG_TO_RAD)
                + Math.cos(latitude1  * DEG_TO_RAD)
                * Math.cos(latitude2 * DEG_TO_RAD)
                * Math.cos(theta * DEG_TO_RAD);
        dist = Math.acos(dist);
        dist = dist * RAD_TO_DEG;
        dist = dist * DEG_TO_KM;
        return dist;
    }
}
