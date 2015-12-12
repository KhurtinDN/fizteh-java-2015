package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import org.mockito.Mockito;

import twitter4j.GeoLocation;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Place;

public class JSONPlace {
    public final String name;
    public final GeoLocation[][] coordinates;
    public final String countryCode;
    
    public JSONPlace(JSONObject place) throws JSONException {
        name = place.getString("name");
        countryCode = place.getString("country");
        JSONArray JSONcoordinates = place.getJSONArray("coordinates");
        coordinates = new GeoLocation[JSONcoordinates.length()][];
        for (int i = 0; i < coordinates.length; ++i) {
            coordinates[i] = new GeoLocation[JSONcoordinates.getJSONArray(i).length()];
            for (int j = 0; j < coordinates[i].length; ++j) {
                JSONArray location = JSONcoordinates.getJSONArray(i).getJSONArray(j);
                coordinates[i][j] = new GeoLocation(location.getInt(0), location.getInt(1));
            }
        }
    }
    
    public Place getMockedPlace() {
        Place place = Mockito.mock(Place.class);
        Mockito.when(place.getBoundingBoxCoordinates()).thenReturn(coordinates);
        Mockito.when(place.getFullName()).thenReturn(name);
        Mockito.when(place.getCountryCode()).thenReturn(countryCode);
        return place;
    }
}
