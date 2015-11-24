package org.mti.hip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import org.mti.hip.model.DiagnosisWrapper;
import org.mti.hip.model.FacilityWrapper;
import org.mti.hip.model.InjuryLocationWrapper;
import org.mti.hip.model.SettlementWrapper;
import org.mti.hip.model.SupplementalsWrapper;
import org.mti.hip.model.UserWrapper;
import org.mti.hip.utils.AlertDialogManager;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSONManager;
import org.mti.hip.utils.NetworkBroadcastReceiver;
import org.mti.hip.utils.StorageManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SuperActivity extends AppCompatActivity {

    public static final String EXTRA_MSG = "extramsg";
    public static String currentUserName;
    private static JSONManager jsonManager;
    private static StorageManager storageManager = getStorageManagerInstance();
    private static HttpClient httpClient;
    public static String facilityName;
    public static String locationName;
    public AlertDialogManager alert = new AlertDialogManager(this);
    private ProgressDialog progressDialog;
    private IntentFilter intentFilter;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private static boolean isConnected;

    public static final int diagId = 0;
    public static final int stiId = 1;
    public static final int chronicDiseaseId = 2;
    public static final int mentalIllnessId = 3;
    public static final int injuryId = 4;
    public static final int injuryLocId = 5;

    private static final String LOCATION_KEY = "locationId";
    private static final String FACILITY_KEY = "facilityId";
    private static final String CLINICIAN_KEY = "clinicianName";
    public static final String FACILITIES_LIST_KEY = "facilitieskey";
    public static final String SETTLEMENT_LIST_KEY = "settlementkey";
    public static final String DIAGNOSIS_LIST_KEY = "diaglistkey";
    public static final String SUPPLEMENTAL_LIST_KEY = "supplistkey";
    public static final String INJURY_LOCATIONS_KEY = "injurylockey";
    public static final String USER_LIST_KEY = "userlistkey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_super);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(buildHeader());
        }
        progressDialog = new ProgressDialog(this);


    }

    private static final String PREFS_NAME = "HipPrefs";

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

    public boolean editTextHasContent(EditText et) {
        return !et.getText().toString().isEmpty();
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
        if(facilityName != null) {
            sb.append(facilityName + "  |  " + getDateNowString());
        }
        if(currentUserName != null) {
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
            progressDialog.setMessage("Please wait...");
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
                    responseString = getHttpClientInstance().post(endpoint, body);
                } catch (IOException e) {
                    this.e = e;
                }
            }
            else if (httpMethod.equals(HttpClient.put)) {
                try {
                    responseString = getHttpClientInstance().put(endpoint, body);
                } catch (IOException e) {
                    this.e = e;
                }
            }
            else {
                try {
                    responseString = getHttpClientInstance().get(endpoint);
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
                alert.showAlert("Error", e.getMessage());
            }
            super.onPostExecute(r);
        }

        public abstract void getResponseString(String response);
    }

    /**
     * Save the last used location to Shared Preferences
     * @param locationId
     */
    public void writeLastUsedLocation(String locationId) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(LOCATION_KEY, locationId).commit();
    }

    /**
     * Save the last used facility to Shared Preferences
     * @param facilityId
     */
    public void writeLastUsedFacility(int facilityId) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt(FACILITY_KEY, facilityId).commit();
    }

    /**
     * Save the last used clinician to Shared Preferences
     * @param clinicianName
     */
    public void writeLastUsedClinician(String clinicianName) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(CLINICIAN_KEY, clinicianName).commit();
    }

    /**
     * Read the last used location from Shared Preferences
     * @return
     */
    public String readLastUsedLocation() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(LOCATION_KEY, "");
    }

    /**
     * Read the last used facility from Shared Preferences
     * @return
     */
    public int readLastUsedFacility() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(FACILITY_KEY, 0);
    }

    public void writeString(String key, String value) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public String readString(String key) {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(key, "");
    }
    /**
     * Read the last used clinician from Shared Preferences
     * @return
     */
    public String readLastUsedClinician() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(CLINICIAN_KEY, "");
    }

    public Object getObjectFromPrefsKey(String key) {
        Class clazz = null;
        if(key.matches(SETTLEMENT_LIST_KEY)) clazz = SettlementWrapper.class;
        if(key.matches(INJURY_LOCATIONS_KEY)) clazz = InjuryLocationWrapper.class;
        if(key.matches(DIAGNOSIS_LIST_KEY)) clazz = DiagnosisWrapper.class;
        if(key.matches(SUPPLEMENTAL_LIST_KEY)) clazz = SupplementalsWrapper.class;
        if(key.matches(FACILITIES_LIST_KEY)) clazz = FacilityWrapper.class;
        if(key.matches(USER_LIST_KEY)) clazz = UserWrapper.class;
        return getJsonManagerInstance().read(readString(key), clazz);
    }

    /**
     *
     * @return True if the device is connected to the internet, False otherwise
     */
    public static Boolean isConnected() {
        return isConnected;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkBroadcastReceiver = new NetworkBroadcastReceiver();
        registerReceiver(networkBroadcastReceiver, intentFilter);
        super.onResume();
    }


    public static void setIsConnected(boolean isConnected) {
        SuperActivity.isConnected = isConnected;
    }




}
