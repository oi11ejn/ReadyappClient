package cs.umu.se;
/**
 * Created by oi11ejn on 2014-11-05.
 */

import android.util.Log;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadyappResourceApplication extends Application {

    /** The list of items is persisted in memory. */
    private final Map<String, Event> events;

    public ReadyappResourceApplication() {
        events = new HashMap<String, Event>();
        try {
            InternalStorage.writeObject(HomeActivity.ha.getApplicationContext(), "events", events);
        } catch (IOException e) {
            Log.e("ReadyResourceApplication", e.getMessage(), e);
        }
    }

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that defines routes.
        Router router = new Router(getContext());

        // Defines a route for the resource "list of items"
        router.attach("/events", MyServerResource.class);

        return router;
    }

    public Map<String, Event> getEvents() { return events; }
}