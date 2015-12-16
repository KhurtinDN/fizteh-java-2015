package model;

import twitter4j.User;

/**
 * Class represents Twitter User object.
 */
public class TwitterUser {
    private Long id;
    private String name;

    public TwitterUser(Long userid, String username) {
        this.id = userid;
        this.name = username;
    }

    public final Long getId() {
        return id;
    }

    public final String getName() {
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
    public final String toString() {
        return "TwitterUser{"
                + "id=" + id
                + ", name='" + name
                + '\''
                + '}';
    }
}
