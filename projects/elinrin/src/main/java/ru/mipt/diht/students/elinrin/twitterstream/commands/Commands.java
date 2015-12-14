package ru.mipt.diht.students.elinrin.twitterstream.commands;


import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

import java.util.HashMap;
import java.util.NoSuchElementException;

public abstract class Commands {
    private static final HashMap<String, Commands> COMMANDS;
    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("--query", new QueryCommand());
        COMMANDS.put("-q", new QueryCommand());
        COMMANDS.put("--place", new PlaceCommand());
        COMMANDS.put("-p", new PlaceCommand());
        COMMANDS.put("--stream", new StreamCommand());
        COMMANDS.put("-s", new StreamCommand());
        COMMANDS.put("--hideRetweets", new HideCommand());
        COMMANDS.put("-hRtws", new HideCommand());
        COMMANDS.put("--limit", new LimitCommand());
        COMMANDS.put("-l", new LimitCommand());
        COMMANDS.put("--help", new HelpCommand());
        COMMANDS.put("-h", new HelpCommand());
        COMMANDS.put("-e", new ExitCommand());
        COMMANDS.put("-exit", new ExitCommand());
    }

    public static Commands fromString(final String[] arguments) {
        if (arguments[0].equals("")) {
            throw new NoSuchElementException("");
        }

        if (COMMANDS.containsKey(arguments[0])) {
            Commands command = COMMANDS.get(arguments[0]);
            if (arguments.length - 1 != command.numberOfArguments()) {
                throw new NoSuchElementException("Unexpected number of arguments: "
                        + command.numberOfArguments() + " required");
            }

            command.putArguments(arguments);
            return command;
        } else {
            throw new NoSuchElementException(arguments[0] + " is unknown command");
        }
    }

    public abstract void execute(TwitterProvider twitter);

    protected void putArguments(final String[] args) {
    }

    protected abstract int numberOfArguments();
}
