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
import java.util.HashMap;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class HomeActivity extends Activity {

    protected static String TAG = "HomeActivity";
    protected UserInfo self;
    protected ListView eventList;
    protected ArrayList<Event> events;
    static HomeActivity ha;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ha=this;
        setContentView(R.layout.home);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        eventList = (ListView) findViewById(R.id.event_list);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //TODO: GOTO -> EVENT
                openEvent(events.get(position));
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
        Attendees[] temp = new Attendees[9];
        for(int i = 0; i < 9; i++) {
            Attendees member = new Attendees();
            member.setUserId("stefan" + i);
            if((i % 2) == 0) {
                member.setReady(true);
            } else {
                member.setReady(false);
            }
            temp[i] = member;
        }

        Event event1 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!f sf asf safd sadf safd assdf saf saf asfd asdf sad fsa", "2015-01-08", "2015-01-06", "berra", temp, "");
        Event event2 = new Event("Hackathon", "MA436", "06:00-24:00", "Hacka, prata och clasha", "2015-01-13", "2015-01-03", "berra",temp, "");
        Event event3 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06","berra", temp, "");
        Event event4 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", "berra",temp, "");
        Event event5 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", "berra",temp, "");
        Event event6 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", "berra",temp, "");
        Event event7 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", "berra",temp, "");
        Event event8 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", "berra",temp, "");
        Event event9 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", "berra",temp, "");
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);
        events.add(event6);
        events.add(event7);
        events.add(event8);
        events.add(event9);
        
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, events);
        eventList.setAdapter(adapter);
    }

    public void updateEvents(View view) {
        try {
            HashMap<String, Event> eventHashMap = (HashMap<String, Event>) InternalStorage.readObject(getApplicationContext(), "events");
            int i = 0;
            for(String key : eventHashMap.keySet()) {
                Log.d(TAG, "EVENT" + i + ": " + key);
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void openEvent(Event event) {
        try {
            InternalStorage.writeObject(getApplicationContext(), "event", event);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
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

    public void createEventActivity() {
        Intent intent = new Intent(this, CreateEventActivity.class);
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
                    ClientResource client = new ClientResource("http://10.0.2.2:8080/");
                    Reference uri = new Reference("http://10.0.2.2:8080/events");
                    Event event = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!", "2015-01-08", "2015-01-06", "berra", null, "");
                    event.incrementClock("berra");
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

    public void restGet(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientResource client = new ClientResource("http://10.0.2.2:8080/");
                    Reference uri = new Reference("http://10.0.2.2:8080/events");

                    client.setReference(uri);
                    client.get();
                } catch(Exception e) {
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
