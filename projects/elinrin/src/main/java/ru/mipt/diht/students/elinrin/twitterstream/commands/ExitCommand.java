package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

public class ExitCommand extends Commands {

    @Override
    public final void execute(final TwitterProvider twitter) {
        throw new IllegalMonitorStateException("Exit");
    }

    @Override
    protected final int numberOfArguments() {
        return 0;
    }
}
