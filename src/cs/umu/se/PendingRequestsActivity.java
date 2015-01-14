package cs.umu.se;

import android.app.Activity;
import android.app.DialogFragment;
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

/**
 * Created by oi11ejn on 2015-01-13.
 */
public class PendingRequestsActivity extends MyBaseActivity {

    private final static String TAG = "PendingRequestsActivity";
    protected UserInfo self;
    protected TextView noRequests;
    protected ListView requestList;
    protected ArrayList<String> friendRequests;
    protected MySimpleArrayAdapter adapter;
    protected Thread run;
    protected String friendId;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            self = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        noRequests = (TextView) findViewById(R.id.no_requests);
        requestList = (ListView) findViewById(R.id.request_list);
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //TODO: Accept / decline request
                DialogFragment newFragment = new FriendRequestDialogFragment();
                Bundle args = new Bundle();
                args.putString("userId", friendRequests.get(position));
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "req:"+friendRequests.get(position));
            }
        });
        friendRequests = new ArrayList<String>();
        run = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> fr = (ArrayList<String>) InternalStorage.readObject(getApplicationContext(), "friendRequests");
                    if(!fr.isEmpty()) {
                        noRequests.setText("");
                        if(fr.size() != friendRequests.size()) {
                            friendRequests = fr;
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        noRequests.setText("No requests pending");
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            //Hämta ut pending request ur storage (friendRequests)
            friendRequests = (ArrayList<String>) InternalStorage.readObject(getApplicationContext(), "friendRequests");
            if(friendRequests.isEmpty()) {
                noRequests.setText("No requests pending");
            }

            //Lista dessa
            adapter = new MySimpleArrayAdapter(this, friendRequests);
            requestList.setAdapter(adapter);
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

    private void openPendingRequests() {
        //Already in pending requests
    }

    public void refresh() {
        runOnUiThread(run);
    }


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

    public void createRequest(View view) {
        //TODO: send new request to user (?)
    }

    public void answerRequest(boolean yesOrNo, String friendId) {
        //TODO: if yesOrNo -> accept/decline
        this.friendId = friendId;
        if(yesOrNo) {
            new HttpAddFriendTask().execute();
            Sender.sendAnswer(yesOrNo, friendId, self.getUserId());
        } else {

        }
    }

    private class HttpAddFriendTask extends AsyncTask<Void, Void, UserInfo> {
        @Override
        protected UserInfo doInBackground(Void... params) {
            try {
                final String url = "https://readyappserver.herokuapp.com/friends/" + self.getUserId() + "/" + friendId;
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
                    //Remove request from list
                    friendRequests = (ArrayList<String>) InternalStorage.readObject(getApplicationContext(), "friendRequests");
                    friendRequests.remove(friendId);
                    InternalStorage.writeObject(getApplicationContext(), "friendRequests", friendRequests);
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (ClassNotFoundException e) {
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
            View rowView = inflater.inflate(R.layout.user_row, parent, false);
            TextView title = (TextView) rowView.findViewById(R.id.row_value);
            title.setText(values.get(position));

            return rowView;
        }
    }
}