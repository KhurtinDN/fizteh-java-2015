package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;

import java.util.HashMap;
import java.util.NoSuchElementException;

public abstract class Commands {
    private static final HashMap<String, Commands> COMMANDS;

    /*
     * [--query|-q <query or keywords for stream>]
     * [--place|-p <location|'nearby'>]
     * [--stream|-s]
     * [--hideRetweets]
     * [--limit|-l <tweets>]
     * [--help|-h]
     */
    
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

    public static Commands fromString(String[] s) throws Exception {
        if (s[0].equals("")) {
            throw new NoSuchElementException("");
        }

        if (COMMANDS.containsKey(s[0])) {
            Commands command = COMMANDS.get(s[0]);
            if (s.length - 1 != command.numberOfArguments()) {
                    throw new NoSuchElementException("Unexpected number of arguments: "
                            + command.numberOfArguments() + " required");
            }
            if ((s.equals("--hideRetweets") || s.equals("-hRtws"))
                    && !((s[1].equals("+")) || (s[1].equals("-")))) {
                throw new NoSuchElementException("Wrong second arguments. Expected + or -. ");
            }
            command.putArguments(s);
            return command;
        } else {
            throw new NoSuchElementException(s[0] + " is unknown command");
        }
    }

    public abstract void execute(TwitterProvider twitter);
    protected void putArguments(String[] args) {
    }
    protected abstract int numberOfArguments();
}