package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;

/**
 * Created by alex on 10.10.15.
 */


public class TwitterArgumentsValidator {
    public static void processArguments(Arguments arguments)
            throws IllegalArgumentException {

        if (!isQueryValid(arguments)) {
            throw new IllegalArgumentException("Задан пустой поисковой запрос");
        }

        GeoLocationInfo geoLocationInfo = null;
        String curLocationName;

        if (!arguments.getLocation().isEmpty()) {
            try {
                if (arguments.getLocation().equals("nearby")) {
                    curLocationName = GeoLocationResolver.getNameOfCurrentLocation();
                } else {
                    curLocationName = arguments.getLocation();
                }

                geoLocationInfo = GeoLocationResolver.getGeoLocation(curLocationName);
            } catch (LocationException e) {
                curLocationName = "World";
                arguments.setFailedDetectionLocationMessage(
                        "Невозможно определить запрашиваемое местоположение\n");
            }
            arguments.setGeoLocationInfo(geoLocationInfo);
            arguments.setCurLocationName(curLocationName);
        }
    }

    private static boolean isQueryValid(Arguments arguments) {
        return (!arguments.getQuery().isEmpty() || arguments.isStream());
    }
}
