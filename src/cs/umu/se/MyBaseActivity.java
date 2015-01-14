package cs.umu.se;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oi11msd on 2015-01-14.
 */
public class MyBaseActivity extends Activity {
    protected ReadyApp readyApp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readyApp = (ReadyApp)this.getApplicationContext();
    }
    protected void onResume() {
        super.onResume();
        readyApp.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = readyApp.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            readyApp.setCurrentActivity(null);
    }
}
