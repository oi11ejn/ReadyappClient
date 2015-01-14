package cs.umu.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by oi11ejn on 2015-01-05.
 */
public class ProfileActivity extends MyBaseActivity {
    public static final String EXTRA_MESSAGE = "cs.umu.se.MESSAGE";
    protected static final String TAG = "ProfileActivity";
    protected ListView friendList;
    protected ArrayList<String> list;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        list = new ArrayList<String>();
        friendList = (ListView) findViewById(R.id.friend_list);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                Log.d(TAG, "klickar på event");
                getUser(list.get(position));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            UserInfo self = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
            TextView userId = (TextView) findViewById(R.id.user_id);
            TextView name = (TextView) findViewById(R.id.name);
            TextView lastName = (TextView) findViewById(R.id.last_name);
            TextView email = (TextView) findViewById(R.id.email);
            TextView lastOnline = (TextView) findViewById(R.id.last_online);
            list.clear();
            userId.setText(self.getUserId());
            name.setText("Name: " + self.getName());
            lastName.setText("Last name: " + self.getLastName());
            email.setText("Email: " + self.getEmail());
            lastOnline.setText("Last online at\n" + self.getLastOnline());
            for(UserId user : self.getFriendList()) {
                list.add(user.toString());
            }
            final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplication(), list);
            friendList.setAdapter(adapter);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
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
            case R.id.pending:
                openPendingRequests();
                return true;
//            case R.id.refresh:
//                refresh();
//                return true;
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

    private void openPendingRequests() {
        Intent intent = new Intent(this, PendingRequestsActivity.class);
        startActivity(intent);
    }

//    public void refresh() {
//        runOnUiThread(run);
//    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void getUser(String user) {
        Intent intent =  new Intent(this, MyFriendActivity.class);
        intent.putExtra(EXTRA_MESSAGE, user);
        startActivity(intent);
    }

    public void showEvents(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void showContacts(View view) {
        Intent intent = new Intent(this, MyFriendsActivity.class);
        startActivity(intent);
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public MySimpleArrayAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.user_row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.user_row, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.row_value);
            textView.setText(values.get(position));

            return rowView;
        }
    }
}
