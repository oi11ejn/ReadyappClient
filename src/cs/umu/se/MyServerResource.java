package cs.umu.se;


import android.util.Log;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by oi11ejn on 2015-01-07.
 */
public class MyServerResource extends BaseResource {
    private static final String TAG = "MyServerResource";
    private String ready;

    public void doInit() {
        this.ready = (String) getRequest().getAttributes().get("ready");

//        getVariants().add(new Variant(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Post
    public Representation post(Representation entity) throws ResourceException {
        try {
            System.out.println("JSAHDKJSA " + ready);
            if(ready == null) {
                // deserialize data
                ObjectMapper mapper = new ObjectMapper(new BsonFactory());
                Event event = mapper.readValue(entity.getStream(), Event.class);

                if(event != null && !getEvents().containsKey(event.getEventName() + event.getCreator())) {
                    Log.d(TAG, "Storing object with key " + event.getEventName() + event.getCreator());
                    getEvents().put(event.getEventName() + event.getCreator(), event);
                    InternalStorage.writeObject(HomeActivity.ha.getApplicationContext(), "events", getEvents());
                    getResponse().setStatus(Status.SUCCESS_CREATED);
                } else if(getEvents().containsKey(event.getEventName() + event.getCreator())) {
                    Log.d(TAG, "Got same event again with wrong method <POST> event key:" + event.getEventName() + event.getCreator());
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                }
            } else {
                //Skicka readystatus objekt ist
                // deserialize data
                ObjectMapper mapper = new ObjectMapper(new BsonFactory());
                ReadyStatus ready = mapper.readValue(entity.getStream(), ReadyStatus.class);

                if(getEvents().containsKey(ready.getEventId())) {
                    Attendees[] attendees = getEvents().get(ready.getEventId()).getAttendees();
                    boolean changed = false;
                    for(Attendees attendee : attendees) {
                        if(attendee.equals(ready.getUserId())) {
                            if(ready.isReady()) {
                                if(!attendee.isReady()) {
                                    attendee.setReady(true);
                                    changed = true;
                                }
                            } else {
                                if(attendee.isReady()) {
                                    attendee.setReady(false);
                                    changed = true;
                                }
                            }
                        }
                    }
                    if(changed) {
                        getEvents().get(ready.getEventId()).setAttendees(attendees);
                        InternalStorage.writeObject(HomeActivity.ha.getApplicationContext(), "events", getEvents());
                        //TODO: PUT CHANGES TO ALL ATTENDEES
                        Sender.sendEvent(getEvents().get(ready.getEventId()), "put");
                        getResponse().setStatus(Status.SUCCESS_CREATED);
                    }
                }
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        } catch (JsonMappingException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (JsonParseException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return getResponseEntity();
    }

    @Get
    public Representation get(Representation entity) throws ResourceException {
        Log.d(TAG, "GETTTETETT");
        return getResponseEntity();
    }

    @Put
    public Representation put(Representation entity) throws ResourceException {
        try {
            // deserialize data
            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
            Event event = mapper.readValue(entity.getStream(), Event.class);

            if(event != null) {
                //check vc (not yet implemented)

                //put event
                HashMap<String, Event> events = (HashMap<String, Event>) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "events");
                events.put(event.getEventName()+event.getCreator(), event);
                getResponse().setStatus(Status.SUCCESS_CREATED);
            } else {
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        } catch (JsonMappingException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (JsonParseException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return getResponseEntity();
    }
}
