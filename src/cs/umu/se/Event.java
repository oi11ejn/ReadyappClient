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
    private String creator;
    private Attendees[] attendees;
    private String eventImage;
//    private VectorClock vc;

    public Event() {
    }


    public Event(String eventName, String location, String duration, String description, String date, String created, String creator, Attendees[] attendees, String eventImage) {
        this.eventName = eventName;
        this.location = location;
        this.duration = duration;
        this.description = description;
        this.date = date;
        this.created = created;
        this.creator = creator;
        this.attendees = attendees;
        this.eventImage = eventImage;
        //Not yet implemented
//        vc = new VectorClock(creator);
//        for(Attendees attendent : attendees) {
//            vc.addUser(attendent.getUserId());
//        }
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

    public String getCreator() { return creator; }

    //Not yet implemented
//    public void incrementClock(String userId) {
//        vc.incrementClock(userId);
//    }

//    public VectorClock getVc() {
//        return vc;
//    }
}
