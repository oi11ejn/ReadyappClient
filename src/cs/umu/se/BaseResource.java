package cs.umu.se;

import org.restlet.resource.ServerResource;

import java.util.List;
import java.util.Map;

/**
 * Created by oi11ejn on 2015-01-08.
 */
public abstract class BaseResource extends ServerResource {
        public BaseResource() {
            super();
        }

        protected Map<String, Event> getEvents() {
            return ((ReadyappResourceApplication) getApplication()).getEvents();
        }

        protected List<String> getFriendRequests() {
            return ((ReadyappResourceApplication) getApplication()).getFriendRequests();
        }
}
