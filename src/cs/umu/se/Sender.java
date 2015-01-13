package cs.umu.se;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by oi11ejn on 2015-01-10.
 */
public class Sender {
    private final static String TAG = "Sender";

    public static void sendEvent(final Event event, final String method) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> ips = (HashMap<String, String>) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "ips");
                    Boolean send;
                    Attendees[] attendees = event.getAttendees();
                    for (Attendees attendee : attendees)
                        Log.d(TAG, "RECIPIENT " + attendee.getUserId());

                    for (Attendees attendee : attendees) {
                        send = false;
                        if (!ips.containsKey(attendee.getUserId())) {
                            final String url = "https://readyappserver.herokuapp.com/ip/" + attendee.getUserId();
                            RestTemplate restTemplate = new RestTemplate();
                            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                            Authentication auth = (Authentication) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "auth");
                            HttpHeaders requestHeader = new HttpHeaders();
                            requestHeader.set("Authorization", auth.getAuth());
                            requestHeader.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                            HttpEntity entity = new HttpEntity(requestHeader);
                            Log.d(TAG, "Trying to get: " + attendee.getUserId());
                            ResponseEntity<IP> response = restTemplate.exchange(url, HttpMethod.GET, entity, IP.class);
                            if (response.getStatusCode().equals(HttpStatus.OK)) {
                                ips.put(attendee.getUserId(), response.getBody().getIp());
                                send = true;
                            }
                        } else {
                            send = true;
                        }
                        if(send) {
                            ClientResource client = new ClientResource("http://" + ips.get(attendee.getUserId()) + "/events/" + event.getEventName() + event.getCreator());
                            Log.d(TAG, "CLIENT: " + ips.get(attendee.getUserId()));
                            //serialize event
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
                            mapper.writeValue(baos, event);

                            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                            Representation rep = new InputRepresentation(bais, MediaType.APPLICATION_OCTET_STREAM);

                            if (method.equalsIgnoreCase("put")) {
                                //only leader putting for now, not putting to leader
                                if(!event.getCreator().equalsIgnoreCase(attendee.getUserId())) {
                                    client.put(rep);
                                }
                            } else if (method.equalsIgnoreCase("post")) {
                                client.post(rep);
                            }
                        }
                    }
                    InternalStorage.writeObject(HomeActivity.ha.getApplicationContext(), "ips", ips);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).start();
    }

    public static void sendReady(final Event event, final String userId, final boolean ready) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> ips = (HashMap<String, String>) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "ips");
                    Boolean send = false;
                    if (!ips.containsKey(event.getCreator())) {
                        final String url = "https://readyappserver.herokuapp.com/ip/" + event.getCreator();
                        RestTemplate restTemplate = new RestTemplate();
                        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                        Authentication auth = (Authentication) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "auth");
                        HttpHeaders requestHeader = new HttpHeaders();
                        requestHeader.set("Authorization", auth.getAuth());
                        requestHeader.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                        HttpEntity entity = new HttpEntity(requestHeader);
                        ResponseEntity<IP> response = restTemplate.exchange(url, HttpMethod.GET, entity, IP.class);
                        if (response.getStatusCode().equals(HttpStatus.OK)) {
                            ips.put(event.getCreator(), response.getBody().getIp());
                            send = true;
                        }
                    } else {
                        send = true;
                    }
                    if(send) {
                        ClientResource client = new ClientResource("http://" + ips.get(event.getCreator()) + "/status/" + ready);
                        ReadyStatus readyStatus = new ReadyStatus(event.getEventName()+event.getCreator(), userId, ready);
                        //serialize event
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
                        mapper.writeValue(baos, readyStatus);

                        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                        Representation rep = new InputRepresentation(bais, MediaType.APPLICATION_OCTET_STREAM);

                        client.post(rep);
                    }
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).start();
    }

    public static void sendReadyCheck(final Event event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> ips = (HashMap<String, String>) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "ips");
                    Boolean send;
                    Attendees[] attendees = event.getAttendees();
                    for (Attendees attendee : attendees)
                        Log.d(TAG, "RECIPIENT " + attendee.getUserId());

                    for (Attendees attendee : attendees) {
                        send = false;
                        if (!ips.containsKey(attendee.getUserId())) {
                            final String url = "https://readyappserver.herokuapp.com/ip/" + attendee.getUserId();
                            RestTemplate restTemplate = new RestTemplate();
                            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                            Authentication auth = (Authentication) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "auth");
                            HttpHeaders requestHeader = new HttpHeaders();
                            requestHeader.set("Authorization", auth.getAuth());
                            requestHeader.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                            HttpEntity entity = new HttpEntity(requestHeader);
                            Log.d(TAG, "Trying to get: " + attendee.getUserId());
                            ResponseEntity<IP> response = restTemplate.exchange(url, HttpMethod.GET, entity, IP.class);
                            if (response.getStatusCode().equals(HttpStatus.OK)) {
                                ips.put(attendee.getUserId(), response.getBody().getIp());
                                send = true;
                            }
                        } else {
                            send = true;
                        }
                        if(send) {
                            ClientResource client = new ClientResource("http://" + ips.get(attendee.getUserId()) + "/ready/" + event.getEventName() + event.getCreator());
                            Log.d(TAG, "CLIENT: " + ips.get(attendee.getUserId()));
                            //serialize event
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectMapper mapper = new ObjectMapper(new BsonFactory());
                            mapper.writeValue(baos, new ReadyCheck(120));

                            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                            Representation rep = new InputRepresentation(bais, MediaType.APPLICATION_OCTET_STREAM);

                            client.post(rep);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).start();
    }
    public static void sendFriendRequest(final String friendId, final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> ips = (HashMap<String, String>) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "ips");
                    Boolean send = false;
                    if (!ips.containsKey(friendId)) {
                        final String url = "https://readyappserver.herokuapp.com/ip/" + friendId;
                        RestTemplate restTemplate = new RestTemplate();
                        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                        Authentication auth = (Authentication) InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "auth");
                        HttpHeaders requestHeader = new HttpHeaders();
                        requestHeader.set("Authorization", auth.getAuth());
                        requestHeader.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                        HttpEntity entity = new HttpEntity(requestHeader);
                        ResponseEntity<IP> response = restTemplate.exchange(url, HttpMethod.GET, entity, IP.class);
                        if (response.getStatusCode().equals(HttpStatus.OK)) {
                            ips.put(friendId, response.getBody().getIp());
                            send = true;
                        }
                    } else {
                        send = true;
                    }
                    if (send) {
                        ClientResource client = new ClientResource("http://" + ips.get(friendId) + "/friends/" + userId);
                        UserId self = new UserId();
                        self.setUserId(userId);

                        //serialize event
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
                        mapper.writeValue(baos, self);

                        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                        Representation rep = new InputRepresentation(bais, MediaType.APPLICATION_OCTET_STREAM);

                        client.post(rep);
                    }
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).start();
    }


}
