package ru.fizteh.fivt.students.vruchtel.TwitterFirst;
/**
 * Created by Серафима on 07.10.2015.
 */
import twitter4j.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import ru.fizteh.fivt.students.vruchtel.moduletests.library.*;

public class Mainclass {

    public static void main(String args[]) throws Exception {
        try {
            twitterStreamArgs = new
                    ru.fizteh.fivt.students.vruchtel.moduletests.library.TwitterStreamArgs();
            twitterStreamArgs.ParseArguments(args);
            //Если после разбора входных параметров, мы что-то выводим на экран, то тут же завершаемся
            System.out.println(twitterStreamArgs.printingResultOfSearchingArguments());
            if(twitterStreamArgs.isSomethingGoingToPrint) {
                System.exit(0);
            }

            System.out.println("Твиты по запросу <" + twitterStreamArgs.getKeyword() + ">:");

            workingModes = new WorkingModes(twitterStreamArgs);

            if (twitterStreamArgs.isStreamUse()) {
                //если задан флаг --stream
                workingModes.workInStreamMode(new OutputStreamWriter(System.out));
            } else {
                //если флаг --stream не задан
                workingModes.workInCommonMode(new OutputStreamWriter(System.out), counter);
            }

            System.exit(0);

        } catch (TwitterException exception) {
            System.err.println("Twitter exception!");
            System.err.println(exception.getErrorMessage() + " " + exception.getErrorCode());
            System.err.println(counter);

        } catch (IOException exception) {
            System.err.println("Problems with printing or reading data!");
        } catch (InterruptedException exception) {
            System.err.println("Problems with sleeping!");
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.out.println(twitterStreamArgs.printingResultOfSearchingArguments());
        }

    }

    private static Twitter twitter;
    private static ru.fizteh.fivt.students.vruchtel.moduletests.library.TwitterStreamArgs
            twitterStreamArgs;
    private static WorkingModes workingModes;

    private static int counter;
}
