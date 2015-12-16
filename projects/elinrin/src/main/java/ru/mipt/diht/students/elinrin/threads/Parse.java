package ru.mipt.diht.students.elinrin.threads;


import ru.mipt.diht.students.elinrin.threads.exception.HandlerOfException;
import ru.mipt.diht.students.elinrin.threads.exception.UserException;

import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Parse {

    static final String USER_MOD = "user";
    static final int SLEEP_TIME = 500;

    private static int checkArgument(final String arguments) throws UserException {
        int number;
        try {
            number = Integer.valueOf(arguments);
            if (number <= 0) {
                throw new UserException("Expected positive number");
            } else {
                return number;
            }
        } catch (NumberFormatException e) {
            throw new UserException("Expected integer number");
        }
    }

    public static int parse(final String[] args) {
        int number = 0;
        if (args.length != 1) {
            try (Scanner in = new Scanner(System.in)) {
                while (true) {
                    System.out.print("$ Counter ");
                    String arguments = in.nextLine().trim();
                    try {
                        number = checkArgument(arguments);
                        break;
                    } catch (UserException e) {
                        HandlerOfException.handler(e, USER_MOD);
                        try {
                            sleep(SLEEP_TIME);
                        } catch (InterruptedException e1) {
                            HandlerOfException.handler(e1);
                        }
                    }
                }
            }
        } else {
            try {
                number = checkArgument(args[0]);
            } catch (UserException e) {
                HandlerOfException.handler(e);
            }
        }
        return number;
    }


}
