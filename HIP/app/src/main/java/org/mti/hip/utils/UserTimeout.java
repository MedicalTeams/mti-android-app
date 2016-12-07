package org.mti.hip.utils;

import android.app.Activity;
import android.content.Intent;

import org.mti.hip.ClinicianSelectionActivity;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UserTimeout {
    private Timer timer;
    private final long RECHECK = 5 * 1000; //10 seconds
    private final long TIMEOUT = 15 * 60 * 1000; //15 minutes
    private Activity activity;
    private Date startTime;

    public UserTimeout(Activity activity) {
        this.activity = activity;
    }

    public void start() {
        startTime = new Date();
        timer = new Timer();
        timer.schedule(new UserTimeoutTask(this), RECHECK);
    }

    public void stop() {
        timer.cancel();
    }

    public void run() {
        Date now = new Date();
        if(now.getTime() - startTime.getTime() > TIMEOUT) {
            activity.startActivity(new Intent(activity, ClinicianSelectionActivity.class));
            activity.finish();
        } else {
            timer = new Timer();
            timer.schedule(new UserTimeoutTask(this), RECHECK);
        }
    }

    private static class UserTimeoutTask extends TimerTask {

        private final UserTimeout userTimeout;

        public UserTimeoutTask(UserTimeout userTimeout)
        {
            this.userTimeout = userTimeout;
        }

        @Override
        public void run() {
            userTimeout.run();
        }
    }
}
