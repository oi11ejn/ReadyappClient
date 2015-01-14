package cs.umu.se;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by oi11msd on 2015-01-13.
 */
public class ReadyCheckFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you ready for " + this.getArguments().getString("ready_check_event") + "?")
                .setPositiveButton(R.string.ready, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readyCheck(true);
                    }
                })
                .setNegativeButton(R.string.not_ready, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readyCheck(false);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void readyCheck(boolean ready) {
        String eventId = this.getArguments().getString("ready_check_event");
        String eventCreator = this.getArguments().getString("ready_check_event_creator");

        try {
            UserInfo myself = (UserInfo)InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "self");
            HashMap<String, Event> events = (HashMap<String, Event>)InternalStorage.readObject(HomeActivity.ha.getApplicationContext(), "events");
            Event event = events.get(eventId + eventCreator);
            Sender.sendReady(event, myself.getUserId(), ready);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
