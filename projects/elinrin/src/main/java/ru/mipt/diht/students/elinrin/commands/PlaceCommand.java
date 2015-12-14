package ru.mipt.diht.students.elinrin.commands;


import ru.mipt.diht.students.elinrin.TwitterProvider;

public class PlaceCommand extends Commands {

    String searchPlace;

    @Override
    public void execute(TwitterProvider twitterPr) {

        twitterPr.changeParameterPlase(searchPlace);

    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        searchPlace = args[1];
    }
}
