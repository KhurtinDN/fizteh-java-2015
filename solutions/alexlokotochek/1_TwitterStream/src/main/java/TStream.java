import runners.QuerySearchRunner;
import runners.StreamRunner;
import jcmdparser.Parser;
import com.beust.jcommander.JCommander;

public class TStream {

    public static void main (String[] args){

        Parser jcp = new Parser();
        new JCommander(jcp, args);

        if (jcp.stream) {
            StreamRunner.runStream(jcp);
        } else {
            QuerySearchRunner.runSearch(jcp);
        }

    }

}
