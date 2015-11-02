package ru.mipt.diht.students.ale3otik.twitter;

import org.json.JSONException;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.ExitException;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;

import java.io.IOException;

/**
 * Created by alex on 10.10.15.
 */


public class TwitterArgumentsValidator {
    public static void processArguments(Arguments arguments) throws ExitException {

        if (!isQueryValid(arguments)) {
            System.out.print("Задан пустой поисковой запрос");
            throw new ExitException();
        }


        GeoLocationInfo geoLocationInfo = null;
        String curLocationName = "";

        if (arguments.getLocation() != "") {
            try {
                if (arguments.getLocation().equals("nearby")) {
                    curLocationName = GeoLocationResolver.getNameOfCurrentLocation();
                } else {
                    curLocationName = arguments.getLocation();
                }

                geoLocationInfo = GeoLocationResolver.getGeoLocation(curLocationName);

            } catch (IOException | LocationException | JSONException e) {
                curLocationName = "World";
                System.out.println("Не могу определить местоположение\n" + "Регион: World ");
            }
        }

        arguments.setGeoLocationInfo(geoLocationInfo);
        arguments.setCurLocationName(curLocationName);

    }

    private static boolean isQueryValid(Arguments arguments) {
        return (!arguments.getQuery().isEmpty() || arguments.isStream());
    }
}
