package cs.umu.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by oi11ejn on 2014-12-29.
 */
public class SearchResultActivity extends Activity {

    private static final String TAG = "SearchResultActivity";
    protected String message;
    protected ListView friendList;
    protected ImageButton addButton;
    protected UserInfo friend;
    protected UserInfo self;
    protected ArrayList<UserId> friends;
    protected ArrayList<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foreign_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        friends = new ArrayList<UserId>();
        list = new ArrayList<String>();
        // Get the message from the intent
        Intent intent = getIntent();
        message = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);
        friendList = (ListView) findViewById(R.id.friend_list);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //TODO: Add action if needed
            }
        });

        addButton = (ImageButton) findViewById(R.id.add_friend_button);
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
        new HttpRequestTask().execute();
    }

    public void addFriend(View view) {
        Log.d(TAG, "Adding " + friend.getUserId() + "as a friend");
        new HttpAddFriendTask().execute();
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
                enableAddButton(!checkFriend(userinfo.getUserId()));
                friends.clear();
                for (UserId user : userinfo.getFriendList()) {
                    friends.add(user);
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

        protected boolean checkFriend(String userId) {
            for(UserId friendId : self.getFriendList()) {
                if(userId.equals(friendId.getUserId())) {
                    Log.d(TAG, "Is your friend");
                    return true;
                }
            }
            Log.d(TAG, "Is not your friend");
            return false;
        }

        protected void enableAddButton(boolean bool) {
            if(bool) {
                addButton.setEnabled(true);
                addButton.setImageResource(R.drawable.ic_action_user_add);
            } else {
                addButton.setEnabled(false);
                addButton.setImageResource(R.drawable.ic_action_user);
            }
        }
    }

    private class HttpAddFriendTask extends AsyncTask<Void, Void, UserInfo> {
        @Override
        protected UserInfo doInBackground(Void... params) {
            try {
                final String url = "https://readyappserver.herokuapp.com/friends/" + self.getUserId() + "/" + friend.getUserId();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders requestHeader = createHeader();
                HttpEntity entity = new HttpEntity(requestHeader);
                ResponseEntity<UserInfo> response = restTemplate.exchange(url, HttpMethod.POST, entity, UserInfo.class);
                if(response.getStatusCode().equals(HttpStatus.CREATED)) {
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
                    addButton.setEnabled(false);
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