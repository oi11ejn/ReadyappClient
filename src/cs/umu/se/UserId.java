package cs.umu.se;

import java.io.Serializable;

/**
 * Created by oi11ejn on 2014-12-30.
 */
public class UserId implements Serializable {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) { this.userId = userId; }

    public String toString() {
        return userId;
    }
}
