package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 10.10.15.
 */

import com.beust.jcommander.JCommander;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.ConnectionFailedException;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.NormalExitException;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.StreamStartFailedException;
import twitter4j.TwitterException;

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
                throw new NormalExitException("Invalid arguments presentation exit");
            }

            if (arguments.isHelp()) {
                jcm.usage();
                throw new NormalExitException("Normal exit");
            }

            TwitterArgumentsValidator.processArguments(arguments);

            StringBuilder informationMessage = new StringBuilder();
            informationMessage.append("Твиты по");
            if (arguments.getQuery().isEmpty()) {
                informationMessage.append(" пустому запросу");
            } else {
                informationMessage.append(" запросу " + "\"" + arguments.getQuery() + "\"");
            }

            if (!arguments.getCurLocationName().isEmpty()) {
                informationMessage.append(" для \"" + arguments.getCurLocationName() + "\"");
            }

            if (arguments.isStream()) {
                TwitterStreamLauncher.streamStart(arguments, informationMessage.toString());
            } else {
                TwitterSingleQuery.printSingleTwitterQuery(arguments, informationMessage.toString());
            }

        } catch (IllegalArgumentException e) {
            ConsoleUtil.printErrorMessage("IllegalArgumentException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (ConnectionFailedException e) {
            ConsoleUtil.printErrorMessage("ConnectionFailedException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (TwitterException e) {
            ConsoleUtil.printErrorMessage("Unhandled TwitterException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (StreamStartFailedException e) {
            ConsoleUtil.printErrorMessage("StreamStartFailedException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (NormalExitException e) {
            ConsoleUtil.printErrorMessage(e.getMessage());
        }
    }
}
