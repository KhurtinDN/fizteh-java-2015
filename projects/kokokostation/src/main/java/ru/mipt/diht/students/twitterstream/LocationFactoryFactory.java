package ru.mipt.diht.students.twitterstream;

/**
 * Created by mikhail on 04.02.16.
 */
public interface LocationFactoryFactory<T extends Location> {
    LocationFactory<T> get();
}
