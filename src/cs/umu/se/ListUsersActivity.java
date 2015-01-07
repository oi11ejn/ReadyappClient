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
import java.util.HashMap;
import java.util.List;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class ListUsersActivity extends Activity {

    private static final String EXTRA_MESSAGE = "cs.umu.se.MESSAGE";
    private static final String TAG = "ListUsersActivity";
    protected String message;
    protected ListView userList;
    protected ArrayList<String> list;
    protected ArrayList<User> users;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listusers);
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
//        list.add(new User("johan123", "idag", true).toString());
//        users.add(new User("johan123", "idag", true));
//        list.add(new User("haldoOWNZ", "igår", false).toString());
//        users.add(new User("haldoOWNZ", "igår", false));
//        final StableArrayAdapter adapter = new StableArrayAdapter(this,
//                android.R.layout.simple_list_item_1, list);
//        String[] test = {list.get(0), list.get(1)};
//        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, list);
//        userList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute(message);
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

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
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
            TextView textView = (TextView) rowView.findViewById(R.id.userinfo);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values.get(position));

            return rowView;
        }
    }


}
