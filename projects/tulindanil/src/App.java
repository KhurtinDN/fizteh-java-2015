import com.beust.jcommander.JCommander;
import main.Commander;
import main.Worker;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class App {

    static String getRawData() throws IOException {
        URL url = new URL("http://ip-api.com/json");
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return br.readLine();
    }

    static String fetchCityName() throws IOException, JSONException {
        JSONObject object = new JSONObject(App.getRawData());
        return object.get("city").toString();
    }

    static String getCity() {
        String cityName = "";
        try {
            cityName = App.fetchCityName();
        } catch (Exception e) {
            System.err.println(e);
        }
        return cityName;
    }

    public static void main(String[] args) {

        Commander commander = new Commander();
        new JCommander(commander, args);

        if (commander.query == null || commander.help) {
            System.out.println("Usage: java twitterStream  [--query|-q <query or keywords for stream>] " +
                    "[--place|-p <location|'nearby'>] " +
                    "[--stream|-s] " +
                    "[--hideRetweets] " +
                    "[--limit|-l <tweets>] " +
                    "[--help|-h]");
            if (commander.query == null)
                System.exit(0);
        }

        if (commander.stream && commander.limit != 0) {
            System.out.println("You are not allowed to use stream and limit at one time");
            System.exit(-1);
        }

        Worker worker;
        if (commander.place == null || commander.place.equals("nearby")) {
            String cityName = App.getCity();
            System.out.println("Hmm....You are in " + cityName + " , right?");
            worker = new Worker(commander.hideRetweets, cityName);
        } else {
            worker = new Worker(commander.hideRetweets, commander.place);
        }

        if (!commander.stream) {
            worker.performQuery(System.out, commander.query, commander.limit);
        } else {
            worker.performStream(System.out, commander.query);
        }
    }
}