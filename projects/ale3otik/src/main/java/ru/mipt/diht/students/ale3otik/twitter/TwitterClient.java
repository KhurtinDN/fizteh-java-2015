package ru.mipt.diht.students.ale3otik.twitter;

import com.beust.jcommander.JCommander;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.ConnectionFailedException;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.HelpCausedException;
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
            try {
                jcm.parse(args);
            } catch (Exception e) {
                throw new HelpCausedException("Invalid arguments presentation exit");
            }

            if (arguments.isHelp()) {
                throw new HelpCausedException("Normal exit");
            }

            TwitterArgumentsValidator.processArguments(arguments);

            StringBuilder informationMessage = new StringBuilder();
            informationMessage.append(arguments.getDetectionLocationMessage()); // empty if all OK
            informationMessage.append("Твиты по");
            if (arguments.getQuery().isEmpty()) {
                informationMessage.append(" пустому запросу");
            } else {
                informationMessage.append(" запросу ")
                        .append("\"").append(arguments.getQuery()).append("\"");
            }

            if (!arguments.getCurLocationName().isEmpty()) {
                informationMessage.append(" для \"")
                        .append(arguments.getCurLocationName())
                        .append("\"");
            }

            if (arguments.isStream()) {
                // run stream handler
                TwitterStream twStream = TwitterStreamFactory.getSingleton();

                TwitterStreamLauncher twStreamLauncher
                        = new TwitterStreamLauncher(twStream, ConsoleUtil.getStdoutConsumer(), arguments, 0);

                twStreamLauncher.streamStart(informationMessage);
            } else {
                // run singleQuery handler
                Twitter twitter = TwitterFactory.getSingleton();
                TwitterSingleQuery twSingleQuery = new TwitterSingleQuery(twitter);
                ConsoleUtil.printIntoStdout(twSingleQuery
                        .getSingleQueryResult(arguments, informationMessage));
            }

        } catch (ConnectionFailedException e) {
            ConsoleUtil.printErrorMessage("ConnectionFailedException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (TwitterException e) {
            ConsoleUtil.printErrorMessage("Unhandled TwitterException");
            ConsoleUtil.printErrorMessage(e.getMessage());
        } catch (HelpCausedException e) {
            StringBuilder helpBuilder = new StringBuilder();
            JCommander jcm = new JCommander(new Arguments());
            jcm.setProgramName("TwitterQueryClient");
            jcm.usage(helpBuilder);

            ConsoleUtil.printIntoStdout(helpBuilder.toString());
            ConsoleUtil.printErrorMessage(e.getMessage());
        }
    }
}
