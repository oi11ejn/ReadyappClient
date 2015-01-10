package cs.umu.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by oi11msd on 2015-01-08.
 */
public class AddFriendsActivity extends Activity{

    protected ListView friends;
    protected ArrayList<String> list;
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
//            }
//        }).start();
    }

    public void finnish(View view) {
        Intent resultIntent = new Intent();
        final MySimpleArrayAdapter adapter = (MySimpleArrayAdapter) friends.getAdapter();
        ArrayList<String> friendsToAdd = new ArrayList<String>();
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.isChecked(i)) {
                friendsToAdd.add(i, adapter.getFriendID(i));
            }
        }
        resultIntent.putStringArrayListExtra("test", friendsToAdd);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;
        private ArrayList<View> rowViews;

        public MySimpleArrayAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.add_friends_row, values);
            this.context = context;
            this.values = values;
            rowViews = new ArrayList<View>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.add_friends_row, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.add_friend_id);
            textView.setText(values.get(position));
            rowViews.add(position, rowView);

            return rowView;
        }

        public boolean isChecked(int position) {
            CheckBox box = (CheckBox) rowViews.get(position).findViewById(R.id.add_friend_checkbox);
            if(box.isChecked()) {
                return true;
            } else {
                return false;
            }
        }

        public String getFriendID(int position) {
            TextView friendID = (TextView) rowViews.get(position).findViewById(R.id.add_friend_id);
            return friendID.getText().toString();
        }
    }

}
