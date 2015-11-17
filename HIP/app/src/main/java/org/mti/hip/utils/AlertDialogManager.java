package org.mti.hip.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by r624513 on 11/4/15.
 */
public class AlertDialogManager {

    private Context context;

    public AlertDialogManager(Context context) {
        this.context = context;
    }

    public void showAlert(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void showErrorReport(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setCancelable(false);
        alert.setTitle("Oops! Something went wrong.");
        alert.setMessage(message);
        alert.setPositiveButton("Send Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Add reporter such as gmail?? Or server endpoint report?? or both??
                /*
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setData(Uri.parse("test@gmail.com"));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "test@gmail.com" });
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "test");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "hello. this is a message sent from my demo app :-)");
                startActivity(sendIntent);
                 */

                dialog.dismiss();
            }
        });



        alert.show();
    }



}
