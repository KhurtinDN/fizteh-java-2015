package ru.mipt.diht.students.tveritinova.TwitterStream;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Query;

import java.io.IOException;
import java.util.List;

public class Location {
    private static final int RADIUS = 50;
    private GeoLocation geoLocation;

    public Location(String loc) {
        Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
                .setAddress(loc).getGeocoderRequest();
        try {
            GeocodeResponse gcResponse = geocoder.geocode(geocoderRequest);
            List<GeocoderResult> gcResult = gcResponse.getResults();
            double latitude = gcResult.get(0)
                    .getGeometry().getLocation().getLat().floatValue();
            double longitude = gcResult.get(0)
                    .getGeometry().getLocation().getLng().floatValue();
            geoLocation = new GeoLocation(latitude, longitude);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public final GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public final void setQueryLocation(Query q) {
        q.setGeoCode(geoLocation, RADIUS, Query.Unit.km);
    }

    public final void setFilterQueryLocation(FilterQuery fq) {
        fq = fq.locations(new double[][] {new double[]
                {geoLocation.getLatitude(), geoLocation.getLongitude()}});
    }
}
