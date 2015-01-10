package cs.umu.se;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by oi11msd on 2015-01-08.
 */
public class AddFriendsActivity extends Activity{
    protected static String TAG = "AddFriendsActivity";
    protected ListView friends;
    protected ArrayList<String> list;
    protected ArrayList<Boolean> test = new ArrayList<Boolean>();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friends);
        // If your minSdkVersion is 11 or higher, instead use:
        // to be able to go back
        getActionBar().setDisplayHomeAsUpEnabled(true);

        friends = (ListView) findViewById(R.id.friend_list);

        list = new ArrayList<String>();
    }

    @Override
    public void onStart() {
        super.onStart();
//        new Thread(new Runnable() {
//            public void run() {
        try {
            UserInfo self  = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");

            list.clear();
            UserId[] friendsIDs = self.getFriendList();
            for(UserId friendID : friendsIDs) {
                list.add(friendID.getUserId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplication(), list);
        friends.setAdapter(adapter);

//        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    final int position, long id) {
//                //TODO: GOTO -> EVENT
//                CheckBox cBox = (CheckBox)view;
//                cBox.setChecked();
//
//                adapter.getView(position, parent, parent);
//            }
//        });
//            }
//        }).start();
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void showEvents(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    public void createEvent(View view) {
        String eventTitle = getIntent().getStringExtra("title");
        String eventDate = getIntent().getStringExtra("date");
        String eventDescription = getIntent().getStringExtra("description");
        String eventDuration = getIntent().getStringExtra("duration");
        String eventLocation = getIntent().getStringExtra("location");
        String eventTime = getIntent().getStringExtra("time");

        final MySimpleArrayAdapter adapter = (MySimpleArrayAdapter) friends.getAdapter();
        ArrayList<String> friendsToInviteIDs = new ArrayList<String>();
        for(int i = 0; i < adapter.getCount(); i++) {
            View theRow = adapter.getView(i, null, (ViewGroup)view.getParent());
            CheckBox test = (CheckBox)theRow.findViewById(R.id.add_friend_checkbox);
            TextView test2 = (TextView)theRow.findViewById(R.id.add_friend_id);
            String name = (String)test2.getText();
            if(test.isChecked()) {
                Log.d(TAG, "Lägger till: " + name);
                friendsToInviteIDs.add(name);
            } else {
                Log.d(TAG, "Lägger ej till: " + name);
            }
        }
        for (String id : friendsToInviteIDs) {
            Log.d(TAG, "KOMpISAR SOM SKA ME " + id);
        }

        Attendees[] attendees = new Attendees[friendsToInviteIDs.size() + 1];
        for(int i = 0; i < friendsToInviteIDs.size(); i++) {
            attendees[i] = new Attendees(friendsToInviteIDs.get(i), false);
        }
        UserInfo self = null;
        try {
            self  = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
            attendees[friendsToInviteIDs.size()] = new Attendees(self.getUserId(), true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Calendar date1 = Calendar.getInstance(TimeZone.getDefault());
        Date date2 = date1.getTime();
        Event event = new Event(eventTitle, eventLocation, eventDuration, eventDescription, eventDate, date2.toString(), self.getUserId() ,attendees, "IMAGE", eventTime);
        Sender.send(attendees, event, "post");

//        HashMap<String, String> friendsToInviteIPs = new HashMap<String, String>();
//        try {
//            friendsToInviteIPs = (HashMap<String, String>) InternalStorage.readObject(getApplicationContext(), "friendsIPs");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        for(int i = 0; i < friendsToInviteIDs.size(); i++) {
//            String friendToAddIP = friendsToInviteIPs.get(friendsToInviteIDs.get(i));
//
//            ClientResource client = new ClientResource("http://" + friendToAddIP + ":8080/events/");
//
//            try {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ObjectMapper mapper = new ObjectMapper(new BsonFactory());
//
//                mapper.writeValue(baos, event);
//
//                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//                Representation rep = new InputRepresentation(bais, org.restlet.data.MediaType.APPLICATION_OCTET_STREAM);
//                client.post(rep);
//                Log.i(TAG, client.getStatus().toString());
//            } catch (IOException e) {
//                Log.e(TAG, e.getMessage(), e);
//            }
//        }

    }

    private class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;
        private HashMap<Integer, Boolean> itemChecked;

        public MySimpleArrayAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.add_friends_row, values);
            this.context = context;
            this.values = values;
            itemChecked = new HashMap<Integer, Boolean>();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.add_friends_row, parent, false);
                holder = new ViewHolder();

                holder.textView = (TextView) convertView.findViewById(R.id.add_friend_id);
                holder.cBox = (CheckBox) convertView.findViewById(R.id.add_friend_checkbox);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.textView.setText(values.get(position));

            holder.cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   // TODO Auto-generated method stub
                   Log.d(TAG, "tjaba" + values.get(position));
                   itemChecked.put(position, isChecked);
                   if (itemChecked.get(position)) {
                       holder.cBox.setChecked(true);
                       Log.d(TAG, "sant");
                   } else {
                       holder.cBox.setChecked(false);
                       Log.d(TAG, "falskt");
                   }
               }
            });

            if(itemChecked.containsKey(position)) {
                holder.cBox.setChecked(itemChecked.get(position));
            }else {
                itemChecked.put(position, false);
                holder.cBox.setChecked(false);
            }

            return convertView;
        }

    }
    static class ViewHolder {
        TextView textView;
        CheckBox cBox;
    }
}
