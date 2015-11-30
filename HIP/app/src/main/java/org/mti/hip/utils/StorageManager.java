package org.mti.hip.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;

import java.io.File;
import java.io.IOException;

/**
 * Created by r624513 on 11/4/15.
 */
public class StorageManager {

    public static final String TALLY_FILENAME = "tally_file";
    public static final String LOG_TAG = "MTI-HIP";

    private Tally tally;

    public StorageManager() {
    }

    public Visit newVisit() {
        Visit visit = new Visit();
        tally.add(visit);

        return tally.get(tally.size() -1);
    }

    public void setTally(Tally tally) {
        this.tally = tally;
    }

    public Tally getTally() {
        return tally;
    }

    public Visit currentVisit() {
        return tally.get(tally.size() -1);
    }

    /**
     * Reads the persisted file from disk and returns is as a String.
     * @param context
     * @return
     */
    public String readTallyToJsonString(Context context) {
        if (isExternalStorageReadable()) {
            File file = new File(context.getExternalFilesDir(null), TALLY_FILENAME);
            try {
                return FileUtils.readFileToString(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.w(LOG_TAG, "External storage is not readable");
        }
        return null;
    }

    /**
     * Persists the JSON string representation of the Tally object to a file on disk
     * @param jsonString
     * @param context
     */
    public void writeTallyJsonToFile(String jsonString, Context context) {
        if (isExternalStorageWritable()) {
            File file = new File(context.getExternalFilesDir(null), TALLY_FILENAME);
            try {
                FileUtils.writeStringToFile(file, jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.w(LOG_TAG, "External storage is not writable");
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     *
     * @param context
     * @return The MAC address of the mobile device
     */
    public static String getMacAddress(Context context) {
        String macAddress = ((WifiManager) (context.getSystemService(Context.WIFI_SERVICE))).getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "";
        }
        return macAddress;
    }

}
