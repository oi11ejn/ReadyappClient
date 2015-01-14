package cs.umu.se;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by oi11ejn on 2015-01-05.
 */
public class CreateEventActivity extends MyBaseActivity {
    protected static String TAG = "CreateEventActivity";
    ArrayList<String> friendsToInviteIDs;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        // If your minSdkVersion is 11 or higher, instead use:
        // to be able to go back
        getActionBar().setDisplayHomeAsUpEnabled(true);
        friendsToInviteIDs = new ArrayList<String>();
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

    public void addFriendsActivity(View view) {

        EditText titleView = (EditText)findViewById(R.id.title_value);
        EditText dateView = (EditText)findViewById(R.id.date_value);
        EditText durationView = (EditText)findViewById(R.id.duration_value);
        EditText locationView = (EditText)findViewById(R.id.location_value);
        EditText descriptionView = (EditText)findViewById(R.id.description_value);
        EditText timeView = (EditText)findViewById(R.id.time_value);

        boolean allSet = true;
        if(TextUtils.isEmpty(titleView.getText().toString().trim())) {
            titleView.setError("must be set");
            allSet = false;
        }
        if(TextUtils.isEmpty(dateView.getText().toString().trim())) {
            dateView.setError("must be set");
            allSet = false;
        }
        if(TextUtils.isEmpty(durationView.getText().toString().trim())) {
            durationView.setError("must be set");
            allSet = false;
        }
        if(TextUtils.isEmpty(locationView.getText().toString().trim())) {
            locationView.setError("must be set");
            allSet = false;
        }
        if(TextUtils.isEmpty(descriptionView.getText().toString().trim())) {
            descriptionView.setError("must be set");
            allSet = false;
        }
        if(TextUtils.isEmpty(timeView.getText().toString().trim())) {
            timeView.setError("must be set");
            allSet = false;
        }
        if(allSet) {
            String eventTitle = titleView.getText().toString();
            String eventDate = dateView.getText().toString();
            String eventDescription = descriptionView.getText().toString();
            String eventDuration = durationView.getText().toString();
            String eventLocation = locationView.getText().toString();
            String eventTime = timeView.getText().toString();

            Intent intent = new Intent(this, AddFriendsActivity.class);
            intent.putExtra("title", eventTitle); //title
            intent.putExtra("date", eventDate); //date
            intent.putExtra("description", eventDescription); //description
            intent.putExtra("duration", eventDuration); //duration
            intent.putExtra("location", eventLocation); //location
            intent.putExtra("time", eventTime); //time

            startActivity(intent);
        }
    }
}
