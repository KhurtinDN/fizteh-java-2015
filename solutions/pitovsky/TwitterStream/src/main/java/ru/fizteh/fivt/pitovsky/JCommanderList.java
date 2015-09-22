package main.java.ru.fizteh.fivt.pitovsky;

import java.util.ArrayList;
import java.util.List;
import com.beust.jcommander.Parameter;

public class JCommanderList {
	@Parameter
	private List<String> parameters = new ArrayList<>();
	
	private final static int MAX_TWEET_LIMIT = 300;
	 
	@Parameter(names = { "-l", "--limit" }, description = "Maximum tweets for out. Only if --stream is disabled")
	private int tweetLimit = MAX_TWEET_LIMIT;
	
	public int getTweetLimit() {
		return tweetLimit;
	}
	 
	@Parameter(names = {"-p", "--place"}, description = "Location of looking for, without args eq nearby - in your location")
	private String place = "anywhere";
	
	public String getPlace() {
		return place;
	}
	
	@Parameter(names = {"-q", "--query"}, description = "Keywords for looking for, separating by whitespaces")
	private String query;
	
	public String getQueryString() {
		return query;
	}
	public String[] getQuery() {
		if (query == null) {
			String[] emptyarr = {};
			return emptyarr;
		}
		return query.split("[\\s,.]+");
	}
	 
	@Parameter(names = {"-s", "--stream"}, description = "Stream mode: every second print new tweets, exit by esc")
	private boolean stream = false;
	
	public boolean isStream() {
		return stream;
	}
	
	@Parameter(names = {"-r", "--hideRetweets"}, description = "Don't print any retweeted posts")
	private boolean hideretweets = false;
	public boolean isRetweetsHidden() {
		return hideretweets;
	}
	
	@Parameter(names = {"-h", "--help"}, description = "Print this page and exit", help = true)
	private boolean help = false;
	
	public boolean isHelp() {
		return help;
	}
}