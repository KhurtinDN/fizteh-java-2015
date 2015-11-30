package ru.mipt.diht.studens;


import twitter4j.Twitter;

public class TwitterProvider {
    private Twitter twitter;
    private boolean hideRetweets;
    private String plase;

    public TwitterProvider(Twitter twitter) {

        this.twitter = twitter;
        hideRetweets = false;
        plase = " ";
    }

    public void changeParameterRetweets(boolean parameter) {
        hideRetweets = parameter;
    }

    public void changeParameterPlase(String parameter) {
        plase = parameter;
    }


    public Twitter twitter() {
        return twitter;
    }

    public boolean isHideRetweets() {
        return hideRetweets;
    }

    public String getPlase() {
        return plase;
    }


}
