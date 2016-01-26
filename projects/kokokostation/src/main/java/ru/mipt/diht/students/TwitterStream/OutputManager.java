package ru.mipt.diht.students.TwitterStream;

import twitter4j.Status;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mikhail on 16.12.15.
 */
class OutputManager {
    private static final String TWITTER_STREAM_HELP = "twitterStream.help";
    private ArgumentInfo argumentInfo;
    private Writer writer;

    OutputManager(ArgumentInfo argumentInfo, Writer writer) {
        this.argumentInfo = argumentInfo;
        this.writer = writer;
    }

    private static String showTweet(Status tweet, boolean showTime) {
        String output = "";

        if (showTime) {
            output += TimeOutputManager.showTime(tweet.getCreatedAt()) + " ";
        }

        output += "@" + makeBlue(tweet.getUser().getName()) + ": ";

        if (tweet.isRetweet()) {
            output += "ретвитнул @" + tweet.getRetweetedStatus().getUser().getName() + ": ";
        }

        output += tweet.getText();

        if (!tweet.isRetweet()) {
            output += " (" + showChunks(tweet.getRetweetCount(), "ретвит", "ов", "а", "") + ")";
        }

        return output;
    }

    private static String makeBlue(String str) {
        return "\033[34m" + str + "\033[0m";
    }

    private static String showChunks(long chunksNum, String root, String ending1, String ending2, String ending3) {
        String output = chunksNum + " " + root;

        if (chunksNum % 10 == 0 || (5 <= chunksNum && chunksNum <= 19)) {
            output += ending1;
        } else {
            if (2 <= chunksNum % 10 && chunksNum % 10 <= 4) {
                output += ending2;
            } else {
                if (chunksNum % 10 == 1) {
                    output += ending3;
                }
            }
        }

        return output;
    }

    void write(String message) {
        try {
            writer.write(message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.err.println("OutputManager can't write a message: " + e.getMessage());
        }
    }

    boolean writeTweet(Status tweet) {
        if (tweet.isRetweet() && argumentInfo.isHideRetweets()) {
            return false;
        } else {
            write(showTweet(tweet, !argumentInfo.isStream()));
            return true;
        }
    }

    void writeHelp() {
        try {
            write(new String(Files.readAllBytes(Paths.get(TWITTER_STREAM_HELP)), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("OutputManager can't read help file: " + e.getMessage());
        }
    }

    private static class TimeOutputManager {
        private static String showTime(Date date) {
            Calendar time = Calendar.getInstance();
            time.setTime(date);

            String output;

            if (time.after(produceCurrentAdd(Calendar.MINUTE, -2))) {
                output = "Только что";
            } else {
                if (time.after(produceCurrentAdd(Calendar.HOUR, -1))) {
                    output = showChunks(minutesPassed(time), "минут", "", "ы", "а") + " назад";
                } else {
                    if (daysMatch(time, Calendar.getInstance())) {
                        output = showChunks(hoursPassed(time), "час", "ов", "а", "") + " назад";
                    } else {
                        if (daysMatch(time, produceCurrentAdd(Calendar.DAY_OF_MONTH, -1))) {
                            output = "Вчера";
                        } else {
                            output = showChunks(daysPassed(time), "д", "ней", "ня", "ень") + " назад";
                        }
                    }
                }
            }

            return output;
        }

        private static Calendar produceCurrentAdd(int field, int amount) {
            Calendar result = Calendar.getInstance();
            result.add(field, amount);
            return result;
        }

        private static long millisecondsPassed(Calendar time) {
            return Calendar.getInstance().getTimeInMillis() - time.getTimeInMillis();
        }

        private static long minutesPassed(Calendar time) {
            return TimeUnit.MILLISECONDS.toMinutes(millisecondsPassed(time));
        }

        private static long hoursPassed(Calendar time) {
            return TimeUnit.MILLISECONDS.toHours(millisecondsPassed(time));
        }

        private static long daysPassed(Calendar time) {
            return TimeUnit.MILLISECONDS.toDays(millisecondsPassed(time));
        }

        private static boolean daysMatch(Calendar day1, Calendar day2) {
            boolean flag = true;
            int[] fields = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};

            for (int i : fields) {
                flag = flag && day1.get(i) == day2.get(i);
            }

            return flag;
        }
    }
}
