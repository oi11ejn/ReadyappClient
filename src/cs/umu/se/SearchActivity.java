package cs.umu.se;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by oi11ejn on 2014-12-29.
 */
public class SearchActivity extends Activity {
    public final static String EXTRA_MESSAGE = "cs.umu.se.MESSAGE";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        // If your minSdkVersion is 11 or higher, instead use:
        // to be able to go back
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void search(View view) {
        Intent intent = new Intent(this, ListUsersActivity.class);
        EditText editText = (EditText) findViewById(R.id.searchString);
        String search = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, search);
        startActivity(intent);
    }
}
