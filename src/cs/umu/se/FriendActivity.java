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
 * Created by oi11ejn on 2014-12-30.
 */
public class FriendActivity extends MyBaseActivity {

    private static final String TAG = "FriendActivity";
    protected ListView friendList;
    protected ArrayList<String> list;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        list = new ArrayList<String>();
        friendList = (ListView) findViewById(R.id.friend_list);

        // If your minSdkVersion is 11 or higher, instead use:
        // to be able to go back
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void list(View view) {
        new HttpRequestTask().execute();
    }


    private class HttpRequestTask extends AsyncTask<Void, Void, UserId[]> {
        @Override
        protected UserId[] doInBackground(Void... params) {
            try {
                EditText editText = (EditText) findViewById(R.id.userString);
                String user = editText.getText().toString();
                final String url = "https://readyappserver.herokuapp.com/friends/" + user;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders requestHeader = createHeader();
                HttpEntity entity = new HttpEntity(requestHeader);
                Log.d(TAG, "Trying to get friends for: " + user);
                ResponseEntity<UserId[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserId[].class);
                if(response.getStatusCode().equals(HttpStatus.OK)) {
                    return response.getBody();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(UserId[] response) {
            list.clear();
            if(response != null) {
                for (UserId user : response) {
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

    public void showEvents(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
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
            View rowView = inflater.inflate(R.layout.simple_row, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.row_value);
            textView.setText(values.get(position));

            return rowView;
        }
    }
}
