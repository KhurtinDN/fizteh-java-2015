package ru.fizteh.fivt.students.tulindanil.twitterstream.library;

import ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions.InvalidLocationException;
import ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions.LocationDefinitionErrorException;
import ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions.NoKeyException;
import ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions.QueryLimitException;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PlaceLocationResolver {
    static final String LOCATION_DEFINITION_ERROR = "Problem while location definition";

    private Map<String, Location> cache = new HashMap<>();
    private String googleMapsKey;
    private HttpReader httpReader;

    static final int MAX_NUMBER_OF_TRIES = 2;

    public PlaceLocationResolver(HttpReader newHttpReader) throws NoKeyException {

        Properties mapsKeys = new Properties();
        try (InputStream inputStream  = this.getClass().getResourceAsStream("/geo.properties")) {
            mapsKeys.load(inputStream);
        } catch (IOException e) {
            throw new NoKeyException();
        }

        googleMapsKey = mapsKeys.getProperty("google");
        if (googleMapsKey == null) {
            throw new NoKeyException();
        }
        httpReader = newHttpReader;
    }

    Location resolvePlaceLocationGoogle(String nameOfLocation)
            throws InvalidLocationException, QueryLimitException,
            LocationDefinitionErrorException, MalformedURLException {
        int numberOfTries = 0;

        do {
            URL googleMapsURL;
            try {
                URI uri = new URI("https", "maps.googleapis.com", "/maps/api/geocode/json",
                        "address=" + nameOfLocation + "&key=" + googleMapsKey, null);
                googleMapsURL = uri.toURL();
            } catch (URISyntaxException e) {
                throw new LocationDefinitionErrorException("Google: " + "Can't make valid"
                        + "url from place. Perhaps, strange symbols are used");
            }

            try {
                String currentInfo = httpReader.httpGet(googleMapsURL.toString());
                JSONObject locationInfo = new JSONObject(currentInfo);

                String status = locationInfo.getString("status");

                if (status.equals("ZERO_RESULTS")) {
                    throw new InvalidLocationException("Google: Unknown place");
                } else if (status.equals("OVER_QUERY_LIMIT")) {
                    throw new QueryLimitException("Google query limit exceeded");
                } else if (status.equals("REQUEST_DENIED")) {
                    if (locationInfo.has("error_message")) {
                        throw new LocationDefinitionErrorException("Google: "
                                + locationInfo.getString("error_message"));
                    } else {
                        throw new LocationDefinitionErrorException("Google: Unexpected request deny");
                    }
                } else if (!status.equals("OK")) {
                    throw new LocationDefinitionErrorException("Google: " + status);
                }

                locationInfo = locationInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location");

                return new Location(Double.parseDouble(locationInfo.getString("lat")),
                        Double.parseDouble(locationInfo.getString("lng")),
                        nameOfLocation);
            } catch (JSONException | IllegalStateException e) {
                ++numberOfTries;
                if (numberOfTries == MAX_NUMBER_OF_TRIES) {
                    throw new LocationDefinitionErrorException("Google: " + LOCATION_DEFINITION_ERROR + " : "
                            + e.getMessage());
                }
            }
        }
        while (numberOfTries < MAX_NUMBER_OF_TRIES);
        throw new LocationDefinitionErrorException(LOCATION_DEFINITION_ERROR);
    }

    public Location resolvePlaceLocation(String nameOfLocation)
            throws InvalidLocationException, LocationDefinitionErrorException,
            MalformedURLException {
        nameOfLocation = nameOfLocation.trim();

        if (nameOfLocation.isEmpty()) {
            throw new InvalidLocationException("empty address");
        }

        if (!cache.containsKey(nameOfLocation)) {
            try {
                Location result = resolvePlaceLocationGoogle(nameOfLocation);
                cache.put(nameOfLocation, result);
            } catch (QueryLimitException | LocationDefinitionErrorException e) {
                System.err.println(e.getMessage());
            } catch (InvalidLocationException e) {
                cache.put(nameOfLocation, null);
            }
        }

        if (cache.get(nameOfLocation) == null) {
            throw new InvalidLocationException("Unknown place");
        }
        return cache.get(nameOfLocation);
    }

    public Location resolveCurrentLocation() throws LocationDefinitionErrorException, MalformedURLException {
        int numberOfTries = 0;
        do {
            URL whatIsMyCityURL = new URL("http://ipinfo.io/json");
            try {
                String currentInfo = httpReader.httpGet(whatIsMyCityURL.toString());
                JSONObject locationInfo = new JSONObject(currentInfo);

                String[] coordinates = locationInfo.getString("loc").split(",");

                return new Location(
                        Double.parseDouble(coordinates[0]),
                        Double.parseDouble(coordinates[1]),
                        locationInfo.getString("city"));
            } catch (IllegalStateException | JSONException e) {
                ++numberOfTries;
                if (numberOfTries == MAX_NUMBER_OF_TRIES) {
                    throw new LocationDefinitionErrorException(e.toString());
                }
            }
        }
        while (numberOfTries < MAX_NUMBER_OF_TRIES);
        throw new LocationDefinitionErrorException(LOCATION_DEFINITION_ERROR);
    }
}
