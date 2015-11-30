package ru.mipt.diht.studens.commands;


import ru.mipt.diht.studens.TwitterProvider;

public class HideCommand extends Commands {
    String parameter;
    @Override
    public void execute(TwitterProvider twitterPr) {
        if (parameter.equals("+")) twitterPr.changeParameterRetweets(true);
        else twitterPr.changeParameterRetweets(false);
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
