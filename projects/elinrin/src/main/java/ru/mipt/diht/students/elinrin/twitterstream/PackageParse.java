package ru.mipt.diht.students.elinrin.twitterstream;


import ru.mipt.diht.students.elinrin.twitterstream.exception.HandlerOfException;
import ru.mipt.diht.students.elinrin.twitterstream.commands.Commands;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class PackageParse {
    static final String USER_MOD = "user";

    public static void parse(final TwitterProvider twitterPr, final String[] args) {


        try {
            ArrayList<String> current = new ArrayList<>();
            for (int i = 0; i < args.length; ++i) {
                current.clear();
                while (i < args.length) {
                    if (!(args[i].contains(";"))) {
                        current.add(args[i]);
                        i++;
                    } else {
                        current.add(args[i].substring(0, args[i].indexOf(";")));
                        break;
                    }
                }
                if (current.isEmpty()) {
                    return;
                }
                String[] com = new String[current.size()];
                com = current.toArray(com);
                try {
                    Commands command = Commands.fromString(com);
                    command.execute(twitterPr);
                } catch (NoSuchElementException e) {
                    HandlerOfException.handler(e, USER_MOD);
                }
            }
        } catch (IllegalMonitorStateException e) {
            System.out.println("Goodbye");
            System.exit(0);
        } catch (IllegalArgumentException e) {
            HandlerOfException.handler("Wrong arguments", e);
        } catch (Exception e) {
            HandlerOfException.handler(e);
        }
    }
}
