package cs.umu.se;

import java.io.Serializable;

/**
 * Created by oi11ejn on 2015-01-06.
 */
public class Event implements Serializable{
    private String eventName;
    private String location;
    private String duration;
    private String description;
    private String date;
    private String created;
    private Attendees[] attendees;
    private String eventImage;

    public Event() {

    }


    public Event(String eventName, String location, String duration, String description, String date, String created, Attendees[] attendees, String eventImage) {
        this.eventName = eventName;
        this.location = location;
        this.duration = duration;
        this.description = description;
        this.date = date;
        this.created = created;
        this.attendees = attendees;
        this.eventImage = eventImage;
    }

    public Attendees[] getAttendees() {
        return attendees;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDate() {
        return date;
    }

    public String getCreated() {
        return created;
    }

    public String getEventImage() {
        return eventImage;
    }

    public String getLocation() {
        return location;
    }

    public String getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

//    public JSONObject toJSON() throws JSONException {
//        JSONObject json = new JSONObject();
//        json.put("eventName", eventName);
//        json.put("location", location);
//        json.put("duration", duration);
//        json.put("description", description);
//        json.put("date", date);
//        json.put("created", created);
//        json.put("attendes", attendees);
//        json.put("eventImage", eventImage);
//
//        return json;
//    }
}
