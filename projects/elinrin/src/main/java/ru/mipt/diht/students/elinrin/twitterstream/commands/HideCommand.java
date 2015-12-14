package ru.mipt.diht.students.elinrin.twitterstream.commands;


import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

public class HideCommand extends Commands {
    String parameter;
    @Override
    public void execute(TwitterProvider twitterPr) {
        if (parameter.equals("+")) {
            twitterPr.changeParameterRetweets(true);
        } else {
            twitterPr.changeParameterRetweets(false);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        parameter = args[1];
    }

}
