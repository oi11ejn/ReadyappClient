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
 * Created by oi11ejn on 2015-01-02.
 */
public class ListUsersActivity extends MyBaseActivity {

    private static final String EXTRA_MESSAGE = "cs.umu.se.MESSAGE";
    private static final String TAG = "ListUsersActivity";
    protected String message;
    protected ListView userList;
    protected ArrayList<String> list;
    protected ArrayList<User> users;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_users);
        getActionBar().setDisplayHomeAsUpEnabled(true);
//        // Get the message from the intent
        Intent intent = getIntent();
        message = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);
        userList = (ListView) findViewById(R.id.userList);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                getUser(users.get(position));
            }
        });
        list = new ArrayList<String>();
        users = new ArrayList<User>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute(message);
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

    protected void getUser(User user) {
        Intent intent =  new Intent(this, SearchResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, user.getUserId());
        startActivity(intent);
    }

    private class HttpRequestTask extends AsyncTask<String, Void, User[]> {
        @Override
        protected User[] doInBackground(String... params) {
            try {
                final String url = "https://readyappserver.herokuapp.com/search/" + params[0];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Log.d(TAG, "Trying to find: " + params[0]);
                HttpHeaders requestHeader = createHeader();
                HttpEntity entity = new HttpEntity(requestHeader);
//                User[] response = restTemplate.getForObject(url, User[].class);
                ResponseEntity<User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class);
                if(response.getStatusCode().equals(HttpStatus.OK)) {
                    return response.getBody();
                } else
                    return null;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(User[] response) {
            if (response != null) {
                users.clear();
                for (User user : response) {
                    users.add(user);
                    list.add(user.toString());
                }
                final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplication(), list);
                userList.setAdapter(adapter);
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
//        private final String[] values;
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
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values.get(position));

            return rowView;
        }
    }


}
