import com.beust.jcommander.JCommander;
import config.Arguments;
import config.Constants;
import config.TwitterConfig;
import core.handling.TweetHandler;
import core.handling.TweetHandlerFactory;
import core.providing.TweetsProvider;
import core.providing.TweetsProviderFactory;
import model.Mode;

import java.io.*;

/**
 * Main Class.
 */

final int LINE_LENGTH = 1024;

public class TwitterStream {

    public static void main(final String[] argsString) {
        extractArguments(argsString);
        Arguments arguments = Arguments.getInstance();

        /* In case of HELP page is requested,
         * just print HELP file content and exit application
         */
        if (arguments.isHelpRequest()) {
            printHelp(System.out);
            System.exit(0);
        }
        try {
            // read and initialize twitter configuration
            TwitterConfig twitterConfig = new TwitterConfig();
            // define working mode of application
            Mode workingMode = (arguments.isStreamMode()) ? Mode.STREAM : Mode.QUERY;
            // get tweets provider for selected working mode and initialize this one
            TweetsProvider tweetsProvider = TweetsProviderFactory.getProvider(workingMode);
            tweetsProvider.init(twitterConfig);
            // get tweet handler for selected working mode
            TweetHandler tweetHandler = TweetHandlerFactory.getHandler(workingMode);
            // start tweets providing and handling
            tweetsProvider.provide(tweetHandler);
        } catch (Exception e) {
            System.err.println("Application TwitterStream has been occasionally crashed "
                    + "with error = \"" + e.getMessage() + "\"");
            System.err.println("Application will be terminated with error code.");
            System.exit(1);
        }
    }

    /**
     * Transforms arguments string to Arguments object with parsed fields.
     * @param argsString arguments strings from the input
     */
    private static void extractArguments(final String[] argsString) {
        JCommander jCommander = new JCommander();
        jCommander.addObject(Arguments.getInstance());
        jCommander.parse(argsString);
    }

    /**
     * Prints the content of HELP file to the passed output stream.
     * @param out stream where HELP page will be printed
     */
    private static void printHelp(OutputStream out) {
        try {
            byte[] buffer = new byte[LINE_LENGTH];
            try (InputStream input = TwitterStream.class.getClassLoader().getResourceAsStream(Constants.HELP_FILE)) {
                for (int length; (length = input.read(buffer)) != -1;) {
                    out.write(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            System.err.println("Problem with reading help file: \"" + e.getMessage() + "\"");
        }
    }
}