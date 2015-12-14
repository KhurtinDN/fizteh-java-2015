package ru.mipt.diht.students.elinrin.twitterstream;


import twitter4j.Twitter;

public class TwitterProvider {
    private Twitter twitter;
    private boolean hideRetweets;
    private String place;

    public TwitterProvider(Twitter twitter) {

        this.twitter = twitter;
        hideRetweets = false;
        place = " ";
    }

    public void changeParameterRetweets(boolean parameter) {
        hideRetweets = parameter;
    }

    public void changeParameterPlase(String parameter) {
        place = parameter;
    }


    public Twitter twitter() {
        return twitter;
    }

    public boolean isHideRetweets() {
        return hideRetweets;
    }

    public String getPlace() {
        return place;
    }


}
