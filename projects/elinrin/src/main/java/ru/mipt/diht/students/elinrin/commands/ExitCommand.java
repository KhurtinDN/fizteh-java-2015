package ru.mipt.diht.students.elinrin.commands;

import ru.mipt.diht.students.elinrin.TwitterProvider;

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
