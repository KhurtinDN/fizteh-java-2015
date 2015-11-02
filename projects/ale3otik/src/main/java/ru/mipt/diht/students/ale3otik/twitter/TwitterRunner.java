package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 10.10.15.
 */

import com.beust.jcommander.JCommander;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.ExitException;

public class TwitterRunner {
    public static void main(String[] args) {
        try {
            System.out.print(
                    ConsoleUtil.Style.BOLD.line(
                            ConsoleUtil.Style.PURPLE.line("\nTwitter 0.1 ::: welcome \n\n")));

            Arguments arguments = new Arguments();
            JCommander jcm = new JCommander(arguments);
            jcm.setProgramName("TwitterQueryClient");

            try {
                jcm.parse(args);
            } catch (Exception e) {
                jcm.usage();
                throw new ExitException();
            }

            if (arguments.isHelp()) {
                jcm.usage();
                throw new ExitException();
            }

            TwitterArgumentsValidator.processArguments(arguments);

            String informationMessage = "Твиты по";
            if (arguments.getQuery().isEmpty()) {
                informationMessage += " пустому запросу";
            } else {
                informationMessage += " запросу " + "\"" + arguments.getQuery() + "\"";
            }

            if (!arguments.getCurLocationName().isEmpty()) {
                informationMessage += " для \"" + arguments.getCurLocationName() + "\"";
            }

            if (arguments.isStream()) {
                TwitterStream.streamStart(arguments, informationMessage);
            } else {
                TwitterSingleQuery.printSingleTwitterQuery(arguments, informationMessage);
            }

        } catch (ExitException e) {
            //debug;
            ConsoleUtil.printErrorMessage("normal by caught exception exit");
        }

    }
}
