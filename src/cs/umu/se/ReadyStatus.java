package cs.umu.se;

/**
 * Created by oi11ejn on 2015-01-11.
 */
public class ReadyStatus {
    private String eventId;
    private String userId;
    private boolean ready;

    public ReadyStatus() {

    }

    public ReadyStatus(String eventId, String userId, boolean ready) {
        this.eventId = eventId;
        this.userId = userId;
        this.ready = ready;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isReady() {
        return ready;
    }
}
