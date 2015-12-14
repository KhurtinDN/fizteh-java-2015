package ru.mipt.diht.students.elinrin;

import ru.mipt.diht.students.elinrin.exception.HandlerException;
import ru.mipt.diht.students.elinrin.commands.Commands;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class InteractiveParse {
    static final String USER_MOD = "user";

    public static void parse(TwitterProvider twitterPr) {
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("$ ");
                String s;
                s = in.nextLine();
                s = s.trim();
                String[] current = s.split("\\s+");
                for (int i = 0; i < current.length; ++i) {
                    current[i].trim();
                }
                try {
                    Commands command = Commands.fromString(current);
                    command.execute(twitterPr);
                } catch (NoSuchElementException e) {
                    HandlerException.handler(e, USER_MOD);
                }
            }
        } catch (IllegalMonitorStateException e) {
            if (e.getMessage().equals("Exit")) {
                in.close();
                System.out.println("Goodbye");
                System.exit(0);
            }
            else
                HandlerException.handler(e);
        } catch (NoSuchElementException e) {
            HandlerException.handler(e);
        } catch (Exception e) {
            in.close();
            HandlerException.handler(e);
        }
        in.close();
    }
}
