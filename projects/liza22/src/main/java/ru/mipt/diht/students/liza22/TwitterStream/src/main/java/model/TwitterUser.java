package model;

import twitter4j.User;

/**
 * Class represents Twitter User object.
 */
public class TwitterUser {
    private Long id;
    private String name;

    public TwitterUser(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Factory method to convert twitter4j.User object to internal model - TwitterUser object.
     * @param twitter4jUser twitter4j.User object
     * @return TwitterUser object
     */
    public static TwitterUser valueOf(User twitter4jUser) {
        return new TwitterUser(twitter4jUser.getId(), twitter4jUser.getName());
    }

    @Override
    public String toString() {
        return "TwitterUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}