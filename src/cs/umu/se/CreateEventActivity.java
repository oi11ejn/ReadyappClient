package cs.umu.se;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oi11ejn on 2015-01-05.
 */
public class CreateEventActivity extends Activity {
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

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                data.get
            }
        }
    }

//    public void addFriendsActivity() {
//        Intent intent = new Intent(this, AddFriendsActivity.class);
//        startActivity(intent);
//    }

    public void createEvent() {

    }

}
