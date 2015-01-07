package cs.umu.se;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oi11ejn on 2015-01-05.
 */
public class NotificationActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        // If your minSdkVersion is 11 or higher, instead use:
        // to be able to go back
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
