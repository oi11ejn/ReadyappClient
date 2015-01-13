package cs.umu.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oi11msd on 2015-01-07.
 */
public class EventActivity extends Activity {

    private final static String TAG = "EventActivity";

    protected ListView friendsInEventList;
    protected ArrayList<Attendees> list;
    protected Event event;
    protected UserInfo self;
    protected MySimpleArrayAdapter adapter;
    protected Thread run;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        friendsInEventList = (ListView) findViewById(R.id.friend_in_event);
        list = new ArrayList<Attendees>();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            event  = (Event) InternalStorage.readObject(getApplicationContext(), "event");
            self = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
            TextView event_Name = (TextView) findViewById(R.id.event_name);
            TextView event_Location = (TextView) findViewById(R.id.event_location);
            TextView event_Date = (TextView) findViewById(R.id.event_date);
            TextView event_Duration = (TextView) findViewById(R.id.event_duration);
            TextView event_Description = (TextView) findViewById(R.id.event_description);
            list.clear();
            event_Name.setText(event.getEventName());
            event_Location.setText("Location: " + event.getLocation());
            event_Date.setText("Date: " + event.getDate());
            event_Duration.setText("Duration: " + event.getDuration());
            event_Description.setText("Description: " + event.getDescription());

            Attendees[] attendees = event.getAttendees();
            for(Attendees attendee : attendees) {
                list.add(attendee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        adapter = new MySimpleArrayAdapter(getApplication(), list);
        friendsInEventList.setAdapter(adapter);
        run = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, Event> eventHashMap = (HashMap<String, Event>) InternalStorage.readObject(getApplicationContext(), "events");
                    Attendees[] attendees1 = event.getAttendees();
                    Attendees[] attendees2 = eventHashMap.get(event.getEventName()+event.getCreator()).getAttendees();
                    boolean changed = false;
                    for(Attendees a1 : attendees1) {
                        for(Attendees a2 : attendees2) {
                            if(a1.getUserId().equalsIgnoreCase(a2.getUserId())) {
                                if(a1.isReady() != a2.isReady()) {
                                    changed = true;
                                }
                            }
                        }
                    }
                    if(changed) {
                        list.clear();
                        event = eventHashMap.get(event.getEventName()+event.getCreator());
                        for(Attendees attendee : attendees2) {
                            list.add(attendee);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        runOnUiThread(run);
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

    public void openSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void refresh() {
        runOnUiThread(run);
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void sendReady(View view) {
        ToggleButton ready = (ToggleButton) findViewById(R.id.ready_button);
        if(ready.isChecked()) {
            //if not creator post ready to creator
            Sender.sendReady(event, self.getUserId(), true);
        } else {
            //if not creator post not ready to creator
            Sender.sendReady(event, self.getUserId(), false);

        }
    }

    public void readyCheck(View view) {
        Sender.sendReadyCheck(event);
    }

    public void showEvents(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<Attendees> {
        private final Context context;
        private final ArrayList<Attendees> values;

        public MySimpleArrayAdapter(Context context, ArrayList<Attendees> values) {
            super(context, R.layout.simple_row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.simple_row, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.row_value);
            textView.setText(values.get(position).getUserId());

            if(values.get(position).isReady()) {
                textView.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.red));
            }
            return rowView;
        }
    }
}
