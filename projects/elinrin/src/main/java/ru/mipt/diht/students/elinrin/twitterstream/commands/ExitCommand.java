package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

public class ExitCommand extends Commands {

    @Override
    public void execute(TwitterProvider twitter) {
        throw new IllegalMonitorStateException("Exit");
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
