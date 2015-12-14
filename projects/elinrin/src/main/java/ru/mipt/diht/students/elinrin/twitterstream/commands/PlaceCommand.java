package ru.mipt.diht.students.elinrin.twitterstream.commands;


import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

public class PlaceCommand extends Commands {

    private String searchPlace;

    @Override
    public final void execute(final TwitterProvider twitterPr) {

        twitterPr.changeParameterPlase(searchPlace);

    }

    @Override
    protected final int numberOfArguments() {
        return 1;
    }

    @Override
    protected final void putArguments(final String[] args) {
        searchPlace = args[1];
    }
}
