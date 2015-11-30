package ru.mipt.diht.studens;


import twitter4j.Twitter;

public class TwitterProvider {
    private Twitter twitter;
    private boolean hideRetweets;
    private String plase;
    private int limit;

    public TwitterProvider(Twitter twitter) {

        this.twitter = twitter;
        hideRetweets = false;
        limit = 0;
        plase = " ";
    }

    public void changeParameterRetweets(boolean parameter) {
        hideRetweets = parameter;
    }

    public void changeParameterPlase(String parameter) {
        plase = parameter;
    }

    public void changeParameterLimit(int parameter) {
        limit = parameter;
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

    public int getLimit() {
        return limit;
    }


}
