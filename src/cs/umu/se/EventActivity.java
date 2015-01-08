package cs.umu.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by oi11msd on 2015-01-07.
 */
public class EventActivity extends Activity {

    protected ListView friendsInEventList;
    protected ArrayList<Attendees> list;
    protected Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        friendsInEventList = (ListView) findViewById(R.id.friend_inEvent);
        list = new ArrayList<Attendees>();
    }

    @Override
    public void onStart() {
        super.onStart();
//        new Thread(new Runnable() {
//            public void run() {
        try {
            event  = (Event) InternalStorage.readObject(getApplicationContext(), "event");

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
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplication(), list);
        friendsInEventList.setAdapter(adapter);
//            }
//        }).start();
    }


    public void showEvents(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
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
