package org.mti.hip.utils;

import android.app.Activity;
import android.content.Intent;

import org.mti.hip.ClinicianSelectionActivity;

import java.util.Timer;
import java.util.TimerTask;

public class UserTimeout {
    private Timer timer;
    private final long TIMEOUT = 900000; //15 minutes
    private Activity activity;

    public UserTimeout(Activity activity) {
        this.activity = activity;
    }

    public void start() {
        timer = new Timer();
        timer.schedule(new UserTimeoutTask(activity), TIMEOUT);
    }

    public void stop() {
        timer.cancel();
    }

    private static class UserTimeoutTask extends TimerTask {

        private final Activity activity;

        public UserTimeoutTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            activity.startActivity(new Intent(activity, ClinicianSelectionActivity.class));
            activity.finish();
        }
    }
}
