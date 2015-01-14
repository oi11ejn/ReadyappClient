package cs.umu.se;

import android.app.Activity;
import android.app.Application;

/**
 * Created by oi11msd on 2015-01-14.
 */
public class ReadyApp extends Application {

    public void onCreate() {
        super.onCreate();
    }

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}
