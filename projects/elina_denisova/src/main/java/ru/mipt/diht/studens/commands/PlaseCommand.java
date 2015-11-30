package ru.mipt.diht.studens.commands;


import ru.mipt.diht.studens.TwitterProvider;

public class PlaseCommand extends Commands {

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
