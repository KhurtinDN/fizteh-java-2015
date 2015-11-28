package ru.mipt.diht.studens.commands;

import ru.mipt.diht.studens.TwitterProvider;

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
