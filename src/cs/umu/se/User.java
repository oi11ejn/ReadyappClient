package cs.umu.se;

import java.io.Serializable;

/**
 * Created by oi11ejn on 2014-12-30.
 */
public class User implements Serializable {
    private String userId;
    private String lastOnline;
    private boolean yourFriend;

    public User() {

    }

    public User(String userId, String lastOnline, boolean yourFriend) {
        this.userId = userId;
        this.lastOnline = lastOnline;
        this.yourFriend = yourFriend;
    }

    public String getUserId() {
        return userId;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public boolean isYourFriend() {
        return yourFriend;
    }

    public String toString() {
        return "Username: " + userId + "\nLast online: " + lastOnline;
    }
}
