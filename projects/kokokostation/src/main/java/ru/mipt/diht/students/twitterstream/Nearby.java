package ru.mipt.diht.students.twitterstream;

import twitter4j.GeoLocation;

/**
 * Created by mikhail on 06.02.16.
 */
public interface Nearby {
    GeoLocation nearby() throws Exception;
}
