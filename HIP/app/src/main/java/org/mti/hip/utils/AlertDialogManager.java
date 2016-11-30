package org.mti.hip.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import org.mti.hip.R;

public class AlertDialogManager {

    private Context context;

    public AlertDialogManager(Context context) {
        this.context = context;
    }

    public void showAlert(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void showPermissionsAlert(DialogInterface.OnClickListener listener) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(context);
        alert.setTitle(context.getString(R.string.permissions_notice));
        alert.setMessage(context.getString(R.string.visits_can_be_entered));
        alert.setNegativeButton(context.getString(R.string.okay), listener);
        alert.show();
    }
}
