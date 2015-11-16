package ru.mipt.diht.students.ale3otik.twitter;

import com.beust.jcommander.JCommander;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.ConnectionFailedException;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.NormalExitException;
import twitter4j.*;

/**
 * Created by alex on 16.11.15.
 */
public class TwitterClient {
    public static void run(String... args) {
        try {
            ConsoleUtil.printIntoStdout(
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
            informationMessage.append(arguments.getDetectionLocationMessage()); // empty if all OK
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
                // run stream handler
                TwitterStream twStream = TwitterStreamFactory.getSingleton();

                TwitterStreamLauncher twStreamLauncher
                        = new TwitterStreamLauncher(twStream, ConsoleUtil.getStdoutConsumer(), arguments, 0);

                twStreamLauncher.streamStart(informationMessage.toString());
            } else {
                // run singleQuery handler
                Twitter twitter = TwitterFactory.getSingleton();
                TwitterSingleQuery twSingleQuery = new TwitterSingleQuery(twitter);
                ConsoleUtil.printIntoStdout(twSingleQuery
                        .getSingleQueryResult(arguments, informationMessage.toString()));
            }

        } catch (IllegalArgumentException e) {
            ConsoleUtil.printErrorMessage("IllegalArgumentException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (ConnectionFailedException e) {
            ConsoleUtil.printErrorMessage("ConnectionFailedException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (
                TwitterException e) {
            ConsoleUtil.printErrorMessage("Unhandled TwitterException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (NormalExitException e) {
            ConsoleUtil.printErrorMessage(e.getMessage());
        }
    }
}
