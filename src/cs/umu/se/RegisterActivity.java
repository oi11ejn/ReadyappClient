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
import org.springframework.web.client.RestTemplate;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class RegisterActivity extends Activity {

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
                EditText usernameField = (EditText) findViewById(R.id.user_id);
                EditText passwordField = (EditText) findViewById(R.id.password);
                String name = nameField.getText().toString();
                String lastName = lastNameField.getText().toString();
                String email = emailField.getText().toString();
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                if(!(name.equalsIgnoreCase("") && lastName.equalsIgnoreCase("")
                        && email.equalsIgnoreCase("") && username.equalsIgnoreCase("")
                        && password.equalsIgnoreCase(""))) {
                    NewUser user = new NewUser(name, lastName, email, username, password);

                    // Create a new RestTemplate instance
                    RestTemplate restTemplate = new RestTemplate();

                    // Add the Jackson and String message converters
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    HttpHeaders requestHeaders = new HttpHeaders();
                    requestHeaders.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity entity = new HttpEntity(user, requestHeaders);

                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                    if(response.getStatusCode().equals(HttpStatus.CREATED)) {
                        return "true";
                    }
                }
            } catch (Exception e) {
                Log.e("RegisterActivity", e.getMessage(), e);
            }

            return "false";
        }

        @Override
        protected void onPostExecute(String response) {
            if(response.equalsIgnoreCase("true")) {
                System.out.println("Server sent following response: " + response);
                //Ändra till att man kommer in till samma ruta som efter login om det gick bra.

                //Anta att det går bra för nu, lägg till auth
                //TODO: spara auth
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        }

    }
}
