package cs.umu.se;


import android.content.Context;
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

/**
 * Created by oi11ejn on 2015-01-07.
 */
public class MyServerResource extends BaseResource {
    private static final String TAG = "MyServerResource";
    private String update;

    public void doInit() {
        this.update = (String) getRequest().getAttributes().get("events");

//        getVariants().add(new Variant(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Post
    public Representation post(Representation entity) throws ResourceException {
        try {
            // deserialize data
            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
            Event event = mapper.readValue(entity.getStream(), Event.class);

            if(event != null && !getEvents().containsKey(event.getEventName() + event.getCreator())) {
                Log.d(TAG, "Storing object with key " + event.getEventName() + event.getCreator());
                getEvents().put(event.getEventName() + event.getCreator(), event);
                Context c = HomeActivity.ha.getApplicationContext();
                InternalStorage.writeObject(c, "events", getEvents());
                getResponse().setStatus(Status.SUCCESS_CREATED);
            } else if(getEvents().containsKey(event.getEventName() + event.getCreator())) {
                Log.d(TAG, "Got same event again with wrong method <POST> event key:" + event.getEventName() + event.getCreator());
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

        return getResponseEntity();
    }
}
