package org.mti.hip.utils;

import android.app.ProgressDialog;
import android.content.Context;

import org.mti.hip.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Custom Progress Dialog.  Used for application to make
 * sure dialog stays open for at least the minimum amount
 * of time so that users can read it (built in delay).  If
 * shown for too short of a time, users can't read it and
 * it is confusing (almost like a blocking toaster popup).
 * Also handles max time limit to remove blocking dialog
 * if code does not properly remove dialog.
 */
public class AdvProgressDialog extends ProgressDialog {

    private long showTime;
    private Timer timer;
    private final long MIN_TIME = 2000; //2 seconds
    private final long MAX_TIME = 30000; //30 seconds

    public AdvProgressDialog(Context context) {
        super(context);
        setCancelable(false);
        setMessage(context.getString(R.string.plz_wait));
        showTime = System.nanoTime();
        timer = new Timer(false);
    }

    /**
     * Show dialog and call ProgressDialog.show along
     * with starting a timer and setting a callback
     * to close dialog after max time.
     */
    @Override
    public void show() {
        super.show();
        showTime = System.nanoTime();
        timer.schedule(new AdvProgressTask(this), MAX_TIME);
    }

    /**
     * Graceful dismisses the dialog.  If the minimum amount
     * of time has not been reached, it will set a timer
     * to dismiss after the minimum time.
     */
    @Override
    public void dismiss() {
        double duration = (System.nanoTime() - showTime) / 1000000;
        if(duration > MIN_TIME) {
            forceDismiss();
        } else {
            timer.schedule(new AdvProgressTask(this), MIN_TIME);
        }
    }

    /**
     * Forces the dismiss of the dialog whether the timeout
     * has been reached or not.
     */
    public void forceDismiss() {
        timer.cancel();
        timer = new Timer(false); // Create a new timer, cancel stops timer thread.
        try {
            if (super.isShowing()) {
                super.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        }
    }

    /**
     * Class to handle dismissing dialog.  Used for setting
     * delayed timers.
     */
    private static class AdvProgressTask extends TimerTask {

        private final AdvProgressDialog dialog;

        public AdvProgressTask(AdvProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            dialog.forceDismiss();
        }
    }
}
