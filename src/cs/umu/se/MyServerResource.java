package cs.umu.se;


import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import java.io.IOException;

/**
 * Created by oi11ejn on 2015-01-07.
 */
public class MyServerResource extends BaseResource {

    private static final String TAG = "MyServerResource";

    public void doInit() {
//        this.owner = (String) getRequest().getAttributes().get("owner");

//        getVariants().add(new Variant(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Post
    public Representation post(Representation entity) throws ResourceException {
        try {
            // deserialize data
            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
            Event event = mapper.readValue(entity.getStream(), Event.class);

            if(event != null) {
                Log.d(TAG, "Event name:" + event.getEventName());

                // Set the response's status and entity
                getResponse().setStatus(Status.SUCCESS_OK);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getResponseEntity();
    }

    @Get
    public Representation get(Representation entity) throws ResourceException {
        Log.d(TAG, "GETTTETETT");
        return getResponseEntity();
    }
}
