package ru.mipt.diht.students.elinrin.twitterstream.commands;


import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

import java.util.NoSuchElementException;

public class HideCommand extends Commands {
    private String parameter;
    @Override
    public final void execute(final TwitterProvider twitterPr) {
        if (parameter.equals("+")) {
            twitterPr.changeParameterRetweets(true);
        } else {
            twitterPr.changeParameterRetweets(false);
        }
    }

    @Override
    protected final int numberOfArguments() {
        return 1;
    }

    @Override
    protected final void putArguments(final String[] args) {
        if (!((args[1].equals("+")) || (args[1].equals("-")))) {
            throw new NoSuchElementException("Wrong second arguments. Expected + or -. ");
        }
        parameter = args[1];
    }

}
