package ru.mipt.diht.students.twitterstream;

/**
 * Created by mikhail on 04.02.16.
 */
public class BoxLocationLocationFactoryFactory implements LocationFactoryFactory<BoxLocation> {
    @Override
    public LocationFactory<BoxLocation> get() {
        return new LocationFactory<>(BoxLocation.class);
    }
}
