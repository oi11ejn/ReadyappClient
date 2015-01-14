package cs.umu.se;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by oi11ejn on 2015-01-14.
 */
public class FriendRequestDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Accept or decline request from: " + getArguments().getString("userId") + " ?")
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Accept the friend request
                        PendingRequestsActivity callingActivity = (PendingRequestsActivity) getActivity();
                        callingActivity.answerRequest(true, getArguments().getString("userId"));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Decline the friend request
                        PendingRequestsActivity callingActivity = (PendingRequestsActivity) getActivity();
                        callingActivity.answerRequest(false, getArguments().getString("userId"));
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
