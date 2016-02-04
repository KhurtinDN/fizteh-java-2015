package ru.mipt.diht.students.twitterstream;

/**
 * Created by mikhail on 04.02.16.
 */
public class CircleLocationLocationFactoryFactory implements LocationFactoryFactory<CircleLocation> {
    @Override
    public LocationFactory<CircleLocation> get() {
        return new LocationFactory<>(CircleLocation.class);
    }
}
