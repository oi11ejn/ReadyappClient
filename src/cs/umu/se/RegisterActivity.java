package cs.umu.se;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.support.Base64;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class RegisterActivity extends Activity {

    protected static String TAG = "RegisterActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // If your minSdkVersion is 11 or higher, instead use:
        // to be able to go back
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void register(View view) {
        new HttpRequestTask().execute();
    }


    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                final String url = "https://readyappserver.herokuapp.com/register";
                EditText nameField = (EditText) findViewById(R.id.name);
                EditText lastNameField = (EditText) findViewById(R.id.lastName);
                EditText emailField = (EditText) findViewById(R.id.email);
                EditText userIdField = (EditText) findViewById(R.id.user_id);
                EditText passwordField = (EditText) findViewById(R.id.password);
                String name = nameField.getText().toString();
                String lastName = lastNameField.getText().toString();
                String email = emailField.getText().toString();
                String userId = userIdField.getText().toString();
                String password = passwordField.getText().toString();

                if(!(name.equalsIgnoreCase("") && lastName.equalsIgnoreCase("")
                        && email.equalsIgnoreCase("") && userId.equalsIgnoreCase("")
                        && password.equalsIgnoreCase(""))) {
                    Log.i(TAG, "testar: " + name + " sdsada " + userId);
                    NewUser newUser = new NewUser(name, lastName, email, userId, password);

                    // Create a new RestTemplate instance
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    HttpHeaders requestHeaders = new HttpHeaders();
                    requestHeaders.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity entity = new HttpEntity(newUser, requestHeaders);

                    ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
//                    restTemplate.postForObject(url, user, String.class);

                    if(response.getStatusCode().equals(HttpStatus.CREATED)) {
                        try {
                            String auth = userId + ":" + password;
                            byte[] encodedAuth = Base64.encodeBytesToBytes(auth.getBytes(Charset.forName("US-ASCII")));
                            String authHeader = "Basic " + new String( encodedAuth );
                            Authentication authentication = new Authentication(authHeader);
                            InternalStorage.writeObject(getApplicationContext(), "auth", authentication);
                        } catch(IOException e) {
                            Log.e("MainActivity", e.getMessage(), e);
                        }
                        return "true";
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return "false";
        }

        @Override
        protected void onPostExecute(String response) {
            if(response.equalsIgnoreCase("true")) {
                //Ändra till att man kommer in till samma ruta som efter login om det gick bra.

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }

    }
}
