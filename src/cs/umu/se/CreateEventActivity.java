package cs.umu.se;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by oi11ejn on 2015-01-05.
 */
public class CreateEventActivity extends Activity {
    protected static String TAG = "CreateEventActivity";
    ArrayList<String> friendsIDs;

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
        friendsIDs = new ArrayList<String>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                friendsIDs = data.getStringArrayListExtra("test");
            }
        }
    }

    public void addFriendsActivity() {
        Intent intent = new Intent(this, AddFriendsActivity.class);
        startActivity(intent);
    }

    public void createEvent() {
        EditText titleView = (EditText)findViewById(R.id.title_value);
        EditText dateView = (EditText)findViewById(R.id.date_value);
        EditText durationView = (EditText)findViewById(R.id.duration_value);
        EditText locationView = (EditText)findViewById(R.id.location_value);
        EditText descriptionView = (EditText)findViewById(R.id.description_value);

        String eventTitle = titleView.getText().toString();
        String eventDate = dateView.getText().toString();
        String eventDescription = descriptionView.getText().toString();
        String eventDuration = durationView.getText().toString();
        String eventLocation = locationView.getText().toString();

        Log.d(TAG,"1");

//        Attendees[] attendees = new Attendees[friendsIDs.size() + 1];
//        for(int i = 0; i < friendsIDs.size(); i++) {
//            attendees[i] = new Attendees(friendsIDs.get(i), false);
//        }
//        try {
//            UserInfo self  = (UserInfo) InternalStorage.readObject(getApplicationContext(), "self");
//            attendees[friendsIDs.size()] = new Attendees(self.getUserId(), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        Calendar date1 = Calendar.getInstance(TimeZone.getDefault());
//        Date date2 = date1.getTime();
//        Event event = new Event(eventTitle, eventLocation, eventDuration, eventDescription, eventDate, date2.toString(), attendees, "IMAGE");
//
//        ClientResource client = new ClientResource("http://10.0.2.2:8080/");
//        client.setReference("http://10.0.2.2:8080/events");
//
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
//
//            mapper.writeValue(baos, event);
//
//            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//            Representation rep = new InputRepresentation(bais, org.restlet.data.MediaType.APPLICATION_OCTET_STREAM);
//            client.post(rep);
//            Log.i(TAG, client.getStatus().toString());
//        } catch (IOException e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
    }

}
