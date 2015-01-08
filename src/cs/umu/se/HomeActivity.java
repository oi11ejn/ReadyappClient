package cs.umu.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import org.restlet.data.Reference;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class HomeActivity extends Activity {

    protected static String TAG = "HomeActivity";
    UserInfo self;
    ListView eventList;
    ArrayList<Event> events;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        eventList = (ListView) findViewById(R.id.event_list);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //TODO: GOTO -> EVENT

            }
        });
        events = new ArrayList<Event>();
        restTestServer();
    }

    @Override
    public void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
        //TODO: Add current events
        //Lägger till påhittade tills vidare
        //String eventName, String location, String duration, String description, String date, String created, Attendees[] attendees, String eventImage
        Event event1 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", null, "");
        Event event2 = new Event("Hackathon", "MA436", "06:00-24:00", "Hacka, prata och clasha", "2015-01-13", "2015-01-03", null, "");
        Event event3 = new Event();
        events.add(event1);
        events.add(event2);
        for(int i = 0; i < 5; i++)
            events.add(event3);
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, events);
        eventList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_add_friend:
                addFriend();
                return true;
            case R.id.action_list_friends:
                listFriends();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void addFriend() {
    }

    private void listFriends() {
        Intent intent = new Intent(this, FriendActivity.class);
        startActivity(intent);
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void sendNotification(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void restTest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientResource client = new ClientResource("http://localhost:8080/");
                    Reference uri = new Reference("http://localhost:8080/events");
                    Event event = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", null, "");
                    //serialize event
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectMapper mapper = new ObjectMapper(new BsonFactory());
                    mapper.writeValue(baos, event);

                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    Representation rep = new InputRepresentation(bais, org.restlet.data.MediaType.APPLICATION_OCTET_STREAM);

                    client.setReference(uri);
                    client.post(rep);
                    Log.i(TAG, client.getStatus().toString());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).start();
    }

    public void restTestServer() {
        // use this to start and trigger a service
        // potentially add data to the intent
        Intent ServiceIntent = new Intent(this, RestService.class);
        startService(ServiceIntent);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                self = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
                String ip = Utils.getIPAddress(true);
                Log.d(TAG, "IP address for device is: " + ip);
                try {
                    InternalStorage.writeObject(getApplicationContext(), "ip", ip);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                final String url = "https://readyappserver.herokuapp.com/ip/" + self.getUserId();
                IP myIp = new IP();
                myIp.setIp(ip);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders requestHeader = createHeader();
                HttpEntity entity = new HttpEntity(myIp, requestHeader);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                    return "true";
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return "false";
        }

        @Override
        protected void onPostExecute(String bool) {
            if (bool.equalsIgnoreCase("true")) {
                Log.d(TAG, "Ip posted successfully");
            } else {
                Log.d(TAG, "Ip not posted");
            }
        }

        protected HttpHeaders createHeader() {
            try {
                Authentication auth = (Authentication) InternalStorage.readObject(getApplicationContext(), "auth");
                HttpHeaders header = new HttpHeaders();
                header.set("Authorization", auth.getAuth());
                header.setContentType(MediaType.APPLICATION_JSON);
                return header;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<Event> {
        private final Context context;
        private final ArrayList<Event> values;

        public MySimpleArrayAdapter(Context context, ArrayList<Event> values) {
            super(context, R.layout.event_row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.event_row, parent, false);
            TextView title = (TextView) rowView.findViewById(R.id.event_title);
            TextView location = (TextView) rowView.findViewById(R.id.event_location);
            TextView duration = (TextView) rowView.findViewById(R.id.event_duration);
            title.setText(values.get(position).getEventName());
            location.setText(values.get(position).getLocation());
            duration.setText(values.get(position).getDuration());

            return rowView;
        }
    }
}
