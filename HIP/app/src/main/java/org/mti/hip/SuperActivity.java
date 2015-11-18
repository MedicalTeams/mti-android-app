package org.mti.hip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import org.mti.hip.model.User;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSONManager;
import org.mti.hip.utils.StorageManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SuperActivity extends AppCompatActivity {

    public static String currentUserName;
    private static JSONManager jsonManager;
    private static StorageManager storageManager = getStorageManagerInstance();
    private static HttpClient httpClient;
    public static String facilityName;


    public User getCurrentUser() {
        return User.userMap.get(currentUserName);
    }

    public static JSONManager getJsonManagerInstance() {
        if (jsonManager == null) {
            jsonManager = new JSONManager();
        }
        return jsonManager;
    }

    public static StorageManager getStorageManagerInstance() {
        if (storageManager == null) {
            storageManager = new StorageManager();
        }
        return storageManager;
    }

    public static HttpClient getHttpClientInstance() {
        if (httpClient == null) {
            httpClient = new HttpClient();
        }
        return httpClient;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super);
        getSupportActionBar().setSubtitle(buildHeader());
        try {
           Log.d("visitdesc", storageManager.writeValueAsString(storageManager.currentVisit()));
        } catch (Exception ignored){

        }

    }

    public boolean editTextHasContent(EditText et) {
        if (et.getText().toString().isEmpty()) {
            return false;
        }
        return true;
    }

    public String getDateNowString() {
        return new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
    }

    private String buildHeader() {
        StringBuffer sb = new StringBuffer();
        if(facilityName != null) {
            sb.append(facilityName + "  |  " + getDateNowString());
        }
        if(getCurrentUser() != null) {
            sb.append("  |  " + getCurrentUser().getName());
        }
        return sb.toString();
    }



}
