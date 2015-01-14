package cs.umu.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by oi11ejn on 2015-01-05.
 */
public class MyFriendActivity extends MyBaseActivity {
    private static final String TAG = "MyFriendActivity";
    protected ListView friendList;
    protected ArrayList<String> list;
    protected UserInfo friend;
    protected UserInfo self;
    private String message;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_friend_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the message from the intent
        Intent intent = getIntent();
        message = intent.getStringExtra(ProfileActivity.EXTRA_MESSAGE);

        //Create list view and supporting list
        list = new ArrayList<String>();
        friendList = (ListView) findViewById(R.id.friend_list);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //TODO: Add action if needed
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            self = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
            new HttpRequestTask().execute();
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

//    public void refresh() {
//        runOnUiThread(run);
//    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
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

    public void removeFriend(View view) {
        new HttpRemoveFriendTask().execute();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, UserInfo> {
        @Override
        protected UserInfo doInBackground(Void... params) {
            try {
                final String url = "https://readyappserver.herokuapp.com/user/" + message;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders requestHeader = createHeader();
                HttpEntity entity = new HttpEntity(requestHeader);
                Log.d("SearchResultActivity", "Trying to get: " + message);
                ResponseEntity<UserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserInfo.class);
                if(response.getStatusCode().equals(HttpStatus.OK)) {
                    return response.getBody();
                } else
                    return null;
            } catch (Exception e) {
                Log.e("SearchResultActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(UserInfo userinfo) {
            if(userinfo != null) {
                friend = userinfo;
                TextView userId = (TextView) findViewById(R.id.user_id);
                TextView name = (TextView) findViewById(R.id.name);
                TextView lastName = (TextView) findViewById(R.id.last_name);
                TextView email = (TextView) findViewById(R.id.email);
                TextView lastOnline = (TextView) findViewById(R.id.last_online);
                userId.setText(userinfo.getUserId());
                name.setText("Name: " + userinfo.getName());
                lastName.setText("Last name: " + userinfo.getLastName());
                email.setText("Email: " + userinfo.getEmail());
                lastOnline.setText("Last online at\n" + userinfo.getLastOnline());
                list.clear();
                for (UserId user : userinfo.getFriendList()) {
                    list.add(user.toString());
                }
                final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplication(), list);
                friendList.setAdapter(adapter);
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

    private class HttpRemoveFriendTask extends AsyncTask<Void, Void, UserInfo> {
        @Override
        protected UserInfo doInBackground(Void... params) {
            try {
                if(self == null)
                    Log.d(TAG, "self == null" );
                if(friend == null)
                    Log.d(TAG, "friend == null" );
                final String url = "https://readyappserver.herokuapp.com/friends/" + self.getUserId() + "/" + friend.getUserId();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders requestHeader = createHeader();
                HttpEntity entity = new HttpEntity(requestHeader);
                Log.d(TAG, "Trying to remove friend: " + friend.getUserId());
                ResponseEntity<UserInfo> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, UserInfo.class);
                if(response.getStatusCode().equals(HttpStatus.OK)) {
                    return response.getBody();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(UserInfo userinfo) {
            if(userinfo != null) {
                try {
                    InternalStorage.writeObject(getApplicationContext(), "self", userinfo);
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
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
            View rowView = inflater.inflate(R.layout.simple_row, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.row_value);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values.get(position));

            return rowView;
        }
    }
}
