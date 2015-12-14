package ru.mipt.diht.students.elinrin.twitterstream;


import twitter4j.Twitter;

public class TwitterProvider {
    private Twitter twitter;
    private boolean hideRetweets;
    private String place;

    public TwitterProvider(final Twitter twitterUser) {

        twitter = twitterUser;
        hideRetweets = false;
        place = " ";
    }

    public final void changeParameterRetweets(final boolean parameter) {
        hideRetweets = parameter;
    }

    public final void changeParameterPlase(final String parameter) {
        place = parameter;
    }


    public final Twitter twitter() {
        return twitter;
    }

    public final boolean isHideRetweets() {
        return hideRetweets;
    }

    public final String getPlace() {
        return place;
    }


}
