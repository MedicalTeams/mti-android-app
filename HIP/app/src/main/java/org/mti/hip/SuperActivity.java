package org.mti.hip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.mti.hip.model.CentreWrapper;
import org.mti.hip.model.DiagnosisWrapper;
import org.mti.hip.model.InjuryLocationWrapper;
import org.mti.hip.model.SettlementWrapper;
import org.mti.hip.model.SupplementalsWrapper;
import org.mti.hip.model.UserWrapper;
import org.mti.hip.utils.AlertDialogManager;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSONManager;
import org.mti.hip.utils.StorageManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SuperActivity extends AppCompatActivity {

    public static final String DEFAULT_LOG_TAG = "MTI-HIP";

    public static String currentUserName;
    private static JSONManager jsonManager;
    private static HashMap<String, StorageManager> storageManagerModes = new HashMap<String, StorageManager>();
    private static HttpClient httpClient;
    public static String facilityName;
    public static String locationName;
    public AlertDialogManager alert = new AlertDialogManager(this);
    public ProgressDialog progressDialog;
    private static boolean isConnected;
    private static String mode;

    public static final int diagId = 0;
    public static final int stiId = 1;
    public static final int chronicDiseaseId = 2;
    public static final int mentalIllnessId = 3;
    public static final int injuryId = 4;
    public static final int injuryLocId = 5;
    public static int injuryListPosition;

    public static final int visitStatusUnsent = 0; // (doesn’t count toward sent value and will try to send)
    public static final int visitStatusSuccess = 1; // (gets counted toward the "sent" value and won't send again)
    public static final int visitStatusDuplicate = 2; // (gets counted toward the "sent" value and won't send again – useful for logging)
    public static final int visitStatusFailure = 3; // (useful for logging and I WILL try to resend this during the next round or on a manual sync – doesn’t count toward the “sent” value in the UI)
    public static final int visitStatusDisabled = 4; // device has been disabled, will try to send again if re-activated

    public static final String deviceActiveCode = "A";

    private static final String LOCATION_KEY = "locationId";
    private static final String FACILITY_KEY = "facilityId";
    private static final String CLINICIAN_KEY = "clinicianName";
    public static final String FACILITY_NAME_KEY = "facnamekey";
    public static final String FACILITIES_LIST_KEY = "facilitieskey";
    public static final String SETTLEMENT_LIST_KEY = "settlementkey";
    public static final String DIAGNOSIS_LIST_KEY = "diaglistkey";
    public static final String SUPPLEMENTAL_LIST_KEY = "supplistkey";
    public static final String INJURY_LOCATIONS_KEY = "injurylockey";
    public static final String USER_LIST_KEY = "userlistkey";
    public static final String DEVICE_STATUS_KEY = "devicestatuskey";
    private static final String MODE_KEY = "modekey";
    public static final String MODE_PROD = "PROD";
    public static final String MODE_TEST = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facilityName = readLastUsedFacilityName();
        currentUserName = readLastUsedClinician();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_super);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(buildHeader());
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected static final String PREFS_NAME = "HipPrefs";

    public static JSONManager getJsonManagerInstance() {
        if (jsonManager == null) {
            jsonManager = new JSONManager();
        }
        return jsonManager;
    }

    public StorageManager getStorageManagerInstance() {
        String mode = getMode();
        if(!storageManagerModes.containsKey(mode)) {
            storageManagerModes.put(mode, new StorageManager(mode));
        }
        return storageManagerModes.get(mode);
    }


    public static HttpClient getHttpClientInstance() {
        if (httpClient == null) {
            httpClient = new HttpClient();
        }
        return httpClient;
    }

    public boolean editTextHasContent(EditText et) {
        return !et.getText().toString().isEmpty();
    }

    public int editTextToInt(EditText et, int defaultVal) {
        if(editTextHasContent(et)) {
            return Integer.valueOf(et.getText().toString());
        } else {
            return defaultVal;
        }
    }

    public String getDateNowString() {
        return new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
    }

    public String getFormattedDate(Date input) {
        return new SimpleDateFormat("dd-MMM-yyyy").format(input);
    }

    public Spanned bold(String input) {
        return Html.fromHtml("<b>" + input + "</b>");
    }

    private String buildHeader() {
        StringBuffer sb = new StringBuffer();
        if(!facilityName.matches("")) {
            sb.append(facilityName + "  |  " + getDateNowString());
        }
        if(!currentUserName.matches("")) {
            sb.append("  |  " + currentUserName);
        }
        return sb.toString();
    }

    public String parseStiContactsTreated(int contactsTreated) {
        return getString(R.string.contacts_treated).concat(": " + contactsTreated);
    }

    public abstract class NetworkTask extends AsyncTask<Void, Void, String> {
        Exception e;

        private String body;
        private String endpoint;
        private String httpMethod;
        private int responseCode = 0;

        public NetworkTask(String endpoint, String httpMethod) {
            this.endpoint = endpoint;
            this.httpMethod = httpMethod;
        }

        public NetworkTask(String body, String endpoint, String httpMethod) {
            this.body = body;
            this.endpoint = endpoint;
            this.httpMethod = httpMethod;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.plz_wait));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Void... params) {
            String responseString = null;
            if(httpMethod.equals(HttpClient.post)) {
                try {
                    responseString = getHttpClientInstance().post(endpoint, body, getIsProductionMode());
                } catch (IOException e) {
                    this.e = e;
                }
            } else if (httpMethod.equals(HttpClient.put)) {
                try {
                    responseString = getHttpClientInstance().put(endpoint, body, getIsProductionMode());
                } catch (IOException e) {
                    this.e = e;
                }
            } else {
                try {
                    responseString = getHttpClientInstance().get(endpoint, getIsProductionMode());
                } catch (IOException e) {
                    this.e = e;
                }
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String r) {
            progressDialog.dismiss();
            if (e == null) {
                getResponseString(r);
            } else if(!isCancelled()){
                setIsConnected(false);
                if(e.getMessage().isEmpty()) {
                    alert.showAlert(getString(R.string.error), "There was a networking issue. Please check your connection and try again.");
                } else {
                    alert.showAlert(getString(R.string.error), e.getMessage());
                }
            }
            super.onPostExecute(r);
        }

        public abstract void getResponseString(String response);
    }

    /**
     * Save the last used location to Shared Preferences
     *
     * @param locationId
     */
    public void writeLastUsedLocation(String locationId) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(getMode() + LOCATION_KEY, locationId).commit();
    }

    /**
     * Save the last used facility to Shared Preferences
     *
     * @param facilityId
     */
    public void writeLastUsedFacility(int facilityId) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt(getMode() + FACILITY_KEY, facilityId).commit();
    }

    /**
     * Save the last used facility name to Shared Preferences
     *
     * @param name
     */
    public void writeLastUsedFacilityName(String name) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(getMode() + FACILITY_NAME_KEY, name).commit();
    }

    public void writeDeviceStatus(String status) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(getMode() + DEVICE_STATUS_KEY, status).commit();
    }

    /**
     * Save the last used clinician to Shared Preferences
     *
     * @param clinicianName
     */
    public void writeLastUsedClinician(String clinicianName) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(getMode() + CLINICIAN_KEY, clinicianName).commit();
    }

    /**
     * Read the last used location from Shared Preferences
     *
     * @return
     */
    public String readLastUsedLocation() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(getMode() + LOCATION_KEY, "");
    }

    /**
     * Read the last used facility from Shared Preferences
     *
     * @return
     */
    public int readLastUsedFacility() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(getMode() + FACILITY_KEY, 0);
    }

    /**
     * Read the last used clinician from Shared Preferences
     *
     * @return
     */
    public String readLastUsedClinician() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(getMode() + CLINICIAN_KEY, "");
    }

    /**
     * Read the last used facility name from Shared Preferences
     *
     * @return
     */
    public String readLastUsedFacilityName() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(getMode() + FACILITY_NAME_KEY, "");
    }

    public void writeString(String key, String value) {
        // WORK AROUND
        if(key.startsWith(getMode())) {
            key = key.substring(4);
        }
        Log.d("WRITE", key);
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(getMode() + key, value).commit();
    }

    public String readString(String key) {
        // WORK AROUND
        if(key.startsWith(getMode())) {
            key = key.substring(4);
        }
        Log.d("READ", key);
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(getMode() + key, "");
    }

    public Object getObjectFromPrefsKey(String key) {
        Class clazz = null;
        if (key.matches(SETTLEMENT_LIST_KEY)) clazz = SettlementWrapper.class;
        if (key.matches(INJURY_LOCATIONS_KEY)) clazz = InjuryLocationWrapper.class;
        if (key.matches(DIAGNOSIS_LIST_KEY)) clazz = DiagnosisWrapper.class;
        if (key.matches(SUPPLEMENTAL_LIST_KEY)) clazz = SupplementalsWrapper.class;
        if (key.matches(FACILITIES_LIST_KEY)) clazz = CentreWrapper.class;
        if (key.matches(USER_LIST_KEY)) clazz = UserWrapper.class;
        return getJsonManagerInstance().read(readString(getMode() + key), clazz);
    }

    /**
     *
     * @return True if the device is connected to the internet, False otherwise
     */
    public static Boolean isConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        SuperActivity.isConnected = isConnected;
    }

    /**
     *
     */
    public void displayMode() {
        if(getMode() == MODE_PROD) {
            findViewById(R.id.dev_mode).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.dev_mode).setVisibility(View.VISIBLE);
        }
    }

    private String getMode() {
        if(mode == null) {
            mode = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(MODE_KEY, "");
            if(mode == "") {
                mode = MODE_PROD;
            }
        }
        return mode;
    }

    private void setMode(String mode) {
        this.mode = mode;
        writeString(MODE_KEY, mode);
    }

    public boolean getIsProductionMode() {
        if(getMode() == MODE_PROD) {
            return true;
        } else {
            return false;
        }
    }

    public void setIsProductionMode(boolean isProductionMode) {
        if(isProductionMode) {
            setMode(MODE_PROD);
        } else {
            setMode(MODE_TEST);
        }
    }
}
