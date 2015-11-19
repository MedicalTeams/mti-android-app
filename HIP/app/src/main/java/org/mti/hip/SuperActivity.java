package org.mti.hip;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.mti.hip.model.User;
import org.mti.hip.utils.AlertDialogManager;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSONManager;
import org.mti.hip.utils.StorageManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SuperActivity extends AppCompatActivity {

    public static final String EXTRA_MSG = "extramsg";
    public static String currentUserName;
    private static JSONManager jsonManager;
    private static StorageManager storageManager = getStorageManagerInstance();
    private static HttpClient httpClient;
    public static String facilityName;
    public AlertDialogManager alert = new AlertDialogManager(this);

    public static final int diagId = 0;
    public static final int stiId = 1;
    public static final int chronicDiseaseId = 2;
    public static final int mentalIllnessId = 3;
    public static final int injuryId = 4;
    public static final int injuryLocId = 5;

    private static final String LOCATION_KEY = "locationId";
    private static final String FACILITY_KEY = "facilityId";
    private static final String CLINICIAN_KEY = "clinicianName";



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

    /**
     * Save the last used location to Shared Preferences
     * @param locationId
     */
    public void writeLastUsedLocation(int locationId) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt(LOCATION_KEY, locationId).commit();
    }

    /**
     * Save the last used facility to Shared Preferences
     * @param facilityId
     */
    public void writeLastUsedFacility(int facilityId) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt(FACILITY_KEY, facilityId).commit();
    }

    /**
     * Save the last used clinician to Shared Preferences
     * @param clinicianName
     */
    public void writeLastUsedClinician(String clinicianName) {
        getPreferences(Context.MODE_PRIVATE).edit().putString(CLINICIAN_KEY, clinicianName).commit();
    }

    /**
     * Read the last used location from Shared Preferences
     * @return
     */
    public int readLastUsedLocation() {
        return getPreferences(Context.MODE_PRIVATE).getInt(LOCATION_KEY, 0);
    }

    /**
     * Read the last used facility from Shared Preferences
     * @return
     */
    public int readLastUsedFacility() {
        return getPreferences(Context.MODE_PRIVATE).getInt(FACILITY_KEY, 0);
    }

    /**
     * Read the last used clinician from Shared Preferences
     * @return
     */
    public String readLastUsedClinician() {
        return getPreferences(Context.MODE_PRIVATE).getString(CLINICIAN_KEY, "");
    }



}
