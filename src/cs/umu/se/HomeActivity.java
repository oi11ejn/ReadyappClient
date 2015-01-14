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
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class HomeActivity extends Activity {

    protected static String TAG = "HomeActivity";
    protected UserInfo self;
    protected TextView self_title;
    protected ListView eventList;
    protected ArrayList<Event> events;
    protected MySimpleArrayAdapter adapter;
    protected Thread run;
    public static HomeActivity ha;
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
        HashMap<String, String> ips = new HashMap<String, String>();
        try {
            InternalStorage.writeObject(getApplicationContext(), "ips", ips);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        self_title = (TextView) findViewById(R.id.user_name);
        try {
            self = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
            self_title.setText(self.getUserId());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        run = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, Event> eventHashMap = (HashMap<String, Event>) InternalStorage.readObject(getApplicationContext(), "events");
                    boolean changed = false;
                    if (!eventHashMap.isEmpty()) {
                        for (String key : eventHashMap.keySet()) {
                            if (!events.contains(eventHashMap.get(key))) {
                                events.add(eventHashMap.get(key));
                                changed = true;
                            }
                        }
                        if (changed) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        new HttpRequestTask().execute();
        restServer();
    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO: Add current events
        //Lägger till påhittade tills vidare
        //String eventName, String location, String duration, String description, String date, String created, Attendees[] attendees, String eventImage
        Attendees[] temp = new Attendees[9];
        for (int i = 0; i < 9; i++) {
            Attendees member = new Attendees();
            member.setUserId("stefan" + i);
            if ((i % 2) == 0) {
                member.setReady(true);
            } else {
                member.setReady(false);
            }
            temp[i] = member;
        }
        events.clear();
        Event event1 = new Event("Beach meetup", "Bettness", "11:00-16:00", "BADA!f sf asf safd sadf safd assdf saf saf asfd asdf sad fsa", "2015-01-08", "2015-01-06", "berra", temp, "", "");
        Event event2 = new Event("Hackathon", "MA436", "06:00-24:00", "Hacka, prata och clasha", "2015-01-13", "2015-01-03", "berra", temp, "", "");
        events.add(event1);
        events.add(event2);
//        try {
//            HashMap<String, Event> eventHashMap = (HashMap<String, Event>) InternalStorage.readObject(getApplicationContext(), "events");
//            for (String key : eventHashMap.keySet()) {
//                events.add(eventHashMap.get(key));
//            }
//        } catch (IOException e) {
//         Log.e(TAG, e.getMessage(), e);
//        } catch (ClassNotFoundException e) {
//         Log.e(TAG, e.getMessage(), e);
//        }

        adapter = new MySimpleArrayAdapter(this, events);
        eventList.setAdapter(adapter);
        runOnUiThread(run);
    }

    public void createEvent(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
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
            case R.id.pending:
                openPendingRequests();
                return true;
            case R.id.refresh:
                refresh();
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

    private void openPendingRequests() {
        Intent intent = new Intent(this, PendingRequestsActivity.class);
        startActivity(intent);
    }

    public void refresh() {
        runOnUiThread(run);
    }


    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void listFriends() {
        Intent intent = new Intent(this, FriendActivity.class);
        startActivity(intent);
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void restServer() {
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
                ip = "10.0.2.2:8080";
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
