package ru.mipt.diht.students.elinrin.twitterstream;

import ru.mipt.diht.students.elinrin.twitterstream.exception.HandlerOfException;
import ru.mipt.diht.students.elinrin.twitterstream.commands.Commands;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class InteractiveParse {
    static final String USER_MOD = "user";

    public static void parse(final TwitterProvider twitterPr) {
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("$ ");
                String arguments;
                arguments = in.nextLine();
                arguments = arguments.trim();
                String[] current = arguments.split("\\s+");
                for (String argument : current) {
                    argument.trim();
                }
                try {
                    Commands command = Commands.fromString(current);
                    command.execute(twitterPr);
                } catch (NoSuchElementException e) {
                    HandlerOfException.handler(e, USER_MOD);
                }
            }
        } catch (IllegalMonitorStateException e) {
            if (e.getMessage().equals("Exit")) {
                in.close();
                System.out.println("Goodbye");
                System.exit(0);
            } else {
                HandlerOfException.handler(e);
            }
        } catch (NoSuchElementException e) {
            HandlerOfException.handler(e);
        } catch (Exception e) {
            in.close();
            HandlerOfException.handler(e);
        }
        in.close();
    }
}
