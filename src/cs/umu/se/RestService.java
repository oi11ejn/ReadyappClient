package cs.umu.se;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Created by oi11ejn on 2015-01-07.
 */
public class RestService extends IntentService {
    protected static final String TAG = "RestService";

    public RestService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.d(TAG, "Server started");
            Component serverComponent = new Component();
            serverComponent.getServers().add(Protocol.HTTP, 8081);
//            final Router router = new Router(serverComponent.getContext().createChildContext());
//            router.attach("/event", MyServerResource.class);
            serverComponent.getDefaultHost().attach(new ReadyappResourceApplication());
            serverComponent.start();
            Log.d("Server", "Server INITIATED HAX: " + serverComponent.getDefaultHost().getServerAddress());
        } catch (Exception e) {
            Log.e("Server", e.getMessage(), e);
        }
    }
}
