package cs.umu.se;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class SettingsActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
