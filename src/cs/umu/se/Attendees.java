package cs.umu.se;

import java.io.Serializable;

/**
 * Created by oi11ejn on 2015-01-06.
 */
public class Attendees implements Serializable {
    private String userId;
    private boolean leader;
    private boolean ready;

    public Attendees() {

    }

    public String getUserId() {
        return userId;
    }

    public boolean isLeader() {
        return leader;
    }

    public boolean isReady() {
        return ready;
    }
}
