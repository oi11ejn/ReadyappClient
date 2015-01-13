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

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by oi11msd on 2015-01-13.
 */
public class MyFriendsActivity extends Activity{

    protected static final String TAG = "MyFriendActivity";
    protected ListView friendList;
    protected ArrayList<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_friends);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        list = new ArrayList<String>();
        friendList = (ListView) findViewById(R.id.friend_list);
    }


    @Override
    public void onStart() {
        super.onStart();
//        new Thread(new Runnable() {
//            public void run() {
        try {
            UserInfo self = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");

            list.clear();

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
//            }
//        }).start();
    }

    public void showEvents(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
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
