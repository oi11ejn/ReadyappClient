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

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // If your minSdkVersion is 11 or higher, instead use:
        // to be able to go back
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void login(View view) {
        new HttpRequestTask().execute();
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, UserInfo> {

        @Override
        protected UserInfo doInBackground(Void... params) {
            try {
                EditText usernameField = (EditText) findViewById(R.id.username);
                EditText passwordField = (EditText) findViewById(R.id.password);
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                if((username != null && password != null) && !(username.equalsIgnoreCase("") && username.equalsIgnoreCase(""))) {
                    final String url = "https://readyappserver.herokuapp.com/login/" + username;

                    // Create a new RestTemplate instance
                    RestTemplate restTemplate = new RestTemplate();

                    // Add the Jackson and String message converters
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    //Adding authentication header
                    HttpHeaders requestHeader = createHeaders(username, password);
                    requestHeader.setContentType(MediaType.APPLICATION_JSON);

                    //set your entity to send
                    HttpEntity entity = new HttpEntity(requestHeader);

                    System.out.println("Trying to login!");
                    // Make the HTTP POST request, sending the JSON as a string, and expecting a string as response
    //                String response = restTemplate.postForObject(url, login.toString(), String.class);
                    ResponseEntity<UserInfo> response = restTemplate.exchange(url, HttpMethod.POST, entity, UserInfo.class);
                    if(response.getStatusCode().equals(HttpStatus.OK)) {
                        try {
                            String auth = username + ":" + password;
                            byte[] encodedAuth = Base64.encodeBytesToBytes(auth.getBytes(Charset.forName("US-ASCII")));
                            String authHeader = "Basic " + new String( encodedAuth );
                            Authentication authentication = new Authentication(authHeader);
                            InternalStorage.writeObject(getApplicationContext(), "auth", authentication);
                        } catch(IOException e) {
                            Log.e("MainActivity", e.getMessage(), e);
                        }
                        return response.getBody();
                    }
                }
            } catch(Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserInfo response) {
            if(response != null) {
                try {
                    InternalStorage.writeObject(getApplicationContext(), "self", response);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        protected HttpHeaders createHeaders(final String username, final String password){
            return new HttpHeaders(){
                {
                    String auth = username + ":" + password;
                    byte[] encodedAuth = Base64.encodeBytesToBytes(auth.getBytes(Charset.forName("US-ASCII")));
                    String authHeader = "Basic " + new String( encodedAuth );
                    set( "Authorization", authHeader );
                }
            };
        }
    }
}
